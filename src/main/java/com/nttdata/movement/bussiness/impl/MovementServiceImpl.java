package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.CustomerService;
import com.nttdata.movement.bussiness.MovementService;
import com.nttdata.movement.bussiness.ProductService;
import com.nttdata.movement.configuration.KafkaProducerConfiguration;
import com.nttdata.movement.model.dto.Customer;
import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import com.nttdata.movement.model.mongo.MovementMongo;
import com.nttdata.movement.model.repository.MovementRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderOptions;

/**
 * Class MovementServiceImpl.
 */
@Service
public class MovementServiceImpl implements MovementService {

  @Autowired
  CustomerService customerService;

  @Autowired
  ProductService productService;

  @Autowired
  MovementRepository movementRepository;

  @Autowired
  private SenderOptions<String, MovementMongo> senderOptions;

  private Mono<MovementMongo> insertMovementDefault(MovementDto movementDto) {
    movementDto.setId(null);
    movementDto.setDate(new Date());
    return movementRepository.insert(MovementDto.transformIntoMongo(movementDto))
            .doOnSuccess(movementMongo -> KafkaProducerConfiguration
                    .senderCreate(senderOptions,
                            KafkaProducerConfiguration.insertRecord(movementMongo))
                    .subscribe());
  }

  @Override
  public Flux<MovementDto> listMovements(String customerId, String productId) {
    return productService.getProductByCustomerAndId(customerId, productId)
            .flatMapMany(product -> {
              if (product.getId() == null
                      || product.getId().isEmpty()
                      || product.getId().equals("")
              ) {
                return Flux.empty();
              } else {
                return movementRepository.findByProductIdOrderByDateDesc(product.getId())
                        .map(MovementDto::transformIntoDto);
              }
            });
  }

  @Override
  public Mono<MovementDto> accountOpening(MovementDto movementDto) {
    return Mono.justOrEmpty(movementDto)
            .defaultIfEmpty(new MovementDto(new Customer(""), new Product("")))
            .map(MovementDto::getCustomer)
            .flatMap(customerService::checkCustomerExistsElseCreate)
            .doOnNext(cus1 -> movementDto.getCustomer().setId(cus1.getId()))
            .doOnNext(cus1 -> movementDto.getProduct().setCustomerId(cus1.getId()))
            .flatMap(cus1 -> productService.validateCustomerCanProduct(movementDto))
            .defaultIfEmpty(new Product(""))
            .flatMap(pro1 ->
                    (pro1.getId() == null || pro1.getId().isEmpty())
                    ? productService.insertProduct(pro1) : Mono.just(pro1))
            .flatMap(product -> {
              Customer customer = movementDto.getCustomer();
              if (product.getId() == null
                      || product.getId().isEmpty()
                      || product.getId().equals("")
                      || customer.getId() == null
                      || customer.getId().isEmpty()
                      || customer.getId().equals("")
              ) {
                return Mono.empty();
              } else {
                movementDto.setConcept(MovementMongo.MOVEMENT_CONCEPT_1);
                movementDto.setType(MovementMongo.MOVEMENT_TYPE_1);
                movementDto.setProduct(product);
                return this.insertMovementDefault(movementDto);
              }
            })
            .map(mov -> {
              MovementDto movement = MovementDto.transformIntoDto(mov);
              BeanUtils.copyProperties(movementDto.getProduct(), movement.getProduct(), "id");
              BeanUtils.copyProperties(movementDto.getCustomer(), movement.getCustomer(), "id");
              return movement;
            });
    //SI EL OJB MOVEMENTMONGO ES NULL O SU ID ES NULL
    // DEBERIA REVERTIRSE EL REGISTRO DEL CLIENTE Y DEL PRODUCTO
  }

  @Override
  public Mono<MovementDto> registerWithdrawal(MovementDto movementDto) {
    movementDto.setConcept(MovementMongo.MOVEMENT_CONCEPT_2);
    movementDto.setType(MovementMongo.MOVEMENT_TYPE_2);
    return this.registerMovementOfAccounts(movementDto);
  }

  @Override
  public Mono<MovementDto> registerDeposit(MovementDto movementDto) {
    movementDto.setConcept(MovementMongo.MOVEMENT_CONCEPT_3);
    movementDto.setType(MovementMongo.MOVEMENT_TYPE_1);
    return this.registerMovementOfAccounts(movementDto);
  }

  private Mono<MovementDto> registerMovementOfAccounts(MovementDto movementDto) {
    return Mono.justOrEmpty(movementDto)
            .defaultIfEmpty(new MovementDto(new Customer(""), new Product("")))
            .map(MovementDto::getCustomer)
            .flatMap(cus -> customerService.getCustomerById(cus.getId()))
            .doOnNext(movementDto::setCustomer)
            .flatMap(cus -> productService
                    .getProductByCustomerAndId(cus.getId(), movementDto.getProduct().getId())
            )
            .doOnNext(movementDto::setProduct)
            .flatMap(product -> {
              Customer customer = movementDto.getCustomer();
              if (product.getId() == null
                      || product.getId().isEmpty()
                      || product.getId().equals("")
                      || customer.getId() == null
                      || customer.getId().isEmpty()
                      || customer.getId().equals("")) {
                return Mono.empty();
              } else if (product.getType().equals(Product.PRODUCT_TYPE_1)) {
                LocalDate today = LocalDate.now();
                Date firstOfMonth = Date.from(today.withDayOfMonth(1)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant());
                Date endOfMonth = Date.from(today.withDayOfMonth(today.lengthOfMonth())
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault()).toInstant());
                return movementRepository
                        .countByProductIdAndDateBetween(product.getId(), firstOfMonth, endOfMonth)
                        .flatMap(count -> {
                          if (product.getMaxMovementLimit() == null) {
                            return Mono.empty();
                          } else if (count < product.getMaxMovementLimit()) {
                            return this.insertMovementDefault(movementDto);
                          } else {
                            return Mono.empty();
                          }
                        });
              } else if (product.getType().equals(Product.PRODUCT_TYPE_2)) {
                return this.insertMovementDefault(movementDto);
              } else if (product.getType().equals(Product.PRODUCT_TYPE_3)) {
                LocalDate today = LocalDate.now();
                if (product.getSingleDayMovement().equals(today.getMonthValue())) {
                  Date startOfDay = Date.from(LocalDateTime
                          .of(today, LocalTime.MIN)
                          .atZone(ZoneId.systemDefault()).toInstant());
                  Date endOfDay = Date.from(LocalDateTime
                          .of(today, LocalTime.MIN)
                          .atZone(ZoneId.systemDefault()).toInstant());
                  return movementRepository
                          .countByProductIdAndDateBetween(product.getId(), startOfDay, endOfDay)
                          .flatMap(count -> {
                            if (count == 0) {
                              return this.insertMovementDefault(movementDto);
                            } else {
                              return Mono.empty();
                            }
                          });
                } else {
                  return Mono.empty();
                }
              } else {
                return Mono.empty();
              }
            })
            .map(mov -> {
              MovementDto movement = MovementDto.transformIntoDto(mov);
              BeanUtils.copyProperties(movementDto.getProduct(), movement.getProduct(), "id");
              BeanUtils.copyProperties(movementDto.getCustomer(), movement.getCustomer(), "id");
              return movement;
            });
  }

  @Override
  public Mono<MovementDto> registerPayment(MovementDto movementDto) {
    movementDto.setConcept(MovementMongo.MOVEMENT_CONCEPT_4);
    movementDto.setType(MovementMongo.MOVEMENT_TYPE_1);
    return this.registerMovementOfCredits(movementDto);
  }

  @Override
  public Mono<MovementDto> registerSpent(MovementDto movementDto) {
    movementDto.setConcept(MovementMongo.MOVEMENT_CONCEPT_5);
    movementDto.setType(MovementMongo.MOVEMENT_TYPE_2);
    return this.registerMovementOfCredits(movementDto);
  }

  private Mono<MovementDto> registerMovementOfCredits(MovementDto movementDto) {
    return Mono.justOrEmpty(movementDto)
            .defaultIfEmpty(new MovementDto(new Customer(""), new Product("")))
            .map(MovementDto::getCustomer)
            .flatMap(cus -> customerService.getCustomerById(cus.getId()))
            .doOnNext(movementDto::setCustomer)
            .flatMap(cus -> productService
                    .getProductByCustomerAndId(cus.getId(), movementDto.getProduct().getId())
            )
            .doOnNext(movementDto::setProduct)
            .flatMap(product -> {
              Customer customer = movementDto.getCustomer();
              if (product.getId() == null
                      || product.getId().isEmpty()
                      || product.getId().equals("")
                      || customer.getId() == null
                      || customer.getId().isEmpty()
                      || customer.getId().equals("")
              ) {
                return Mono.empty();
              } else if (movementDto.getType().equals(MovementMongo.MOVEMENT_TYPE_2)
                      && product.getType().equals(Product.PRODUCT_TYPE_5)
              ) {
                return Mono.empty();
              } else if (movementDto.getType().equals(MovementMongo.MOVEMENT_TYPE_2)
                      && product.getType().equals(Product.PRODUCT_TYPE_4)
                      && movementDto.getAmount() > product.getCreditLimit()
              ) {
                return Mono.empty();
              } else {
                return this.insertMovementDefault(movementDto);
              }
            })
            .map(mov -> {
              MovementDto movement = MovementDto.transformIntoDto(mov);
              BeanUtils.copyProperties(movementDto.getProduct(), movement.getProduct(), "id");
              BeanUtils.copyProperties(movementDto.getCustomer(), movement.getCustomer(), "id");
              return movement;
            });
  }

}
