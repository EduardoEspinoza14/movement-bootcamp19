package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.CustomerService;
import com.nttdata.movement.bussiness.ProductService;
import com.nttdata.movement.model.dto.Customer;
import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import com.nttdata.movement.model.mongo.MovementMongo;
import com.nttdata.movement.model.repository.MovementRepository;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class ProductServiceImpl.
 */
@Service
public class ProductServiceImpl implements ProductService {

  private static final String CIRCUIT_BREAKER_SERVICE_PRODUCT = "cbServiceProduct";

  private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

  @Value("${api.product.baseUri}")
  private String baseUri;

  @Value("${api.product.savingsAccountUri}")
  private String savingsAccountUri;

  @Value("${api.product.checkingAccountUri}")
  private String checkingAccountUri;

  @Value("${api.product.fixedTermUri}")
  private String fixedTermUri;

  @Value("${api.product.cardUri}")
  private String cardUri;

  @Value("${api.product.loanUri}")
  private String loanUri;

  @Autowired
  WebClient.Builder webClientBuilder;

  @Autowired
  ReactiveResilience4JCircuitBreakerFactory reactiveCircuitBreakerFactory;

  @Autowired
  CustomerService customerService;

  @Autowired
  MovementRepository movementRepository;


  @Override
  public Flux<Product> getProductsByCustomer(String customerId) {
    return webClientBuilder.build()
            .get()
            .uri(baseUri + "/{customerId}", customerId)
            .retrieve()
            .bodyToFlux(Product.class)
            .transform(it -> reactiveCircuitBreakerFactory
                    .create(CIRCUIT_BREAKER_SERVICE_PRODUCT)
                    .run(it, this::productsFallback)
            );
  }

  @Override
  public Mono<Product> getProductByCustomerAndId(String customerId, String productId) {
    return customerService.getCustomerById(customerId)
            .flatMap(customer -> webClientBuilder.build()
                    .get()
                    .uri(baseUri + "/{customerId}/{productId}", customer.getId(), productId)
                    .retrieve()
                    .bodyToMono(Product.class))
            .transform(it -> reactiveCircuitBreakerFactory
                    .create(CIRCUIT_BREAKER_SERVICE_PRODUCT)
                    .run(it, this::productFallback)
            );
  }

  @Override
  public Mono<Product> insertProduct(Product product) {
    String uri;
    product.setId(null);
    product.setStart_date(new Date());
    if (product.getType() == null) {
      return Mono.empty();
    }
    if (product.getType().equals(Product.PRODUCT_TYPE_1)) {
      uri = savingsAccountUri;
    } else if (product.getType().equals(Product.PRODUCT_TYPE_2)) {
      uri = checkingAccountUri;
    } else if (product.getType().equals(Product.PRODUCT_TYPE_3)) {
      uri = fixedTermUri;
    } else if (product.getType().equals(Product.PRODUCT_TYPE_4)) {
      uri = cardUri;
    } else if (product.getType().equals(Product.PRODUCT_TYPE_5)) {
      uri = loanUri;
    } else {
      return Mono.empty();
    }
    return webClientBuilder.build()
            .post()
            .uri(uri).contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(product))
            .retrieve()
            .bodyToMono(Product.class)
            .transform(it -> reactiveCircuitBreakerFactory
                    .create(CIRCUIT_BREAKER_SERVICE_PRODUCT)
                    .run(it, this::productFallback)
            );
  }

  @Override
  public Mono<Product> validateCustomerCanProduct(MovementDto movementDto) {
    return Mono.just(movementDto)
            .map(MovementDto::getCustomer)
            .flatMap(customer -> {
              if (movementDto.getProduct() == null
                      || movementDto.getProduct().getType() == null
                      || movementDto.getProduct().getType().isEmpty()
              ) {
                return Mono.empty();
              } else if (customer.getType().equals(Customer.CUSTOMER_TYPE_1)) {
                if (movementDto.getProduct().getType().equals(Product.PRODUCT_TYPE_4)) {
                  return Mono.just(movementDto.getProduct());
                } else {
                  return this.getProductsByCustomer(customer.getId()).collectList()
                          .flatMap(products -> {
                            boolean repeated = products.stream()
                                    .anyMatch(product ->
                                            product.getType()
                                                    .equals(movementDto.getProduct().getType())
                                    );
                            if (repeated) {
                              return Mono.empty();
                            } else {
                              return Mono.just(movementDto.getProduct());
                            }
                          });
                }
              } else if (customer.getType().equals(Customer.CUSTOMER_TYPE_2)) {
                if (movementDto.getProduct().getType().equals(Product.PRODUCT_TYPE_1)
                        || movementDto.getProduct().getType().equals(Product.PRODUCT_TYPE_3)) {
                  return Mono.empty();
                } else {
                  return Mono.just(movementDto.getProduct());
                }
              } else {
                return Mono.empty();
              }
            });
  }

  @Override
  public Mono<Double> getAvailableBalance(String customerId, String productId) {
    return getProductByCustomerAndId(customerId, productId)
            .flatMap(product -> {
              if (product.getId() == null
                      || product.getId().isEmpty()
                      || product.getId().equals("")
              ) {
                return Mono.empty();
              } else {
                return movementRepository.findByProductId(product.getId())
                        .map(MovementDto::transformIntoDto)
                        .collectList()
                        .flatMap(movements -> {
                          double income = movements
                                  .stream()
                                  .filter(mov -> mov.getType()
                                          .equals(MovementMongo.MOVEMENT_TYPE_1)
                                  ).mapToDouble(MovementDto::getAmount).sum();
                          double expenses = movements
                                  .stream()
                                  .filter(mov -> mov.getType()
                                          .equals(MovementMongo.MOVEMENT_TYPE_2)
                                  ).mapToDouble(MovementDto::getAmount).sum();
                          double initial = (product.getType().equals(Product.PRODUCT_TYPE_4))
                                  ? product.getCreditLimit() : 0.0;
                          if (product.getType().equals(Product.PRODUCT_TYPE_1)
                                  || product.getType().equals(Product.PRODUCT_TYPE_2)
                                  || product.getType().equals(Product.PRODUCT_TYPE_3)
                                  || product.getType().equals(Product.PRODUCT_TYPE_4)
                          ) {
                            return Mono.just(initial - expenses + income);
                          }
                          return Mono.empty();
                        });
              }
            });
  }

  private Flux<Product> productsFallback(Throwable e) {
    log.info("PRODUCT SERVICE IS BREAKER - FLUX");
    return Flux.empty();
  }

  private Mono<Product> productFallback(Throwable e) {
    log.info("PRODUCT SERVICE IS BREAKER - MONO");
    return Mono.empty();
  }

}
