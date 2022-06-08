package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.CustomerService;
import com.nttdata.movement.bussiness.MovementService;
import com.nttdata.movement.bussiness.ProductService;
import com.nttdata.movement.model.dto.Customer;
import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import com.nttdata.movement.model.mongo.MovementMongo;
import com.nttdata.movement.model.repository.MovementRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;

@Service
public class MovementServiceImpl implements MovementService {

    @Autowired
    CustomerService customerService;

    @Autowired
    ProductService productService;

    @Autowired
    MovementRepository movementRepository;

    @Override
    public Flux<MovementDto> listMovements(String productId) {
        return null;
    }

    @Override
    public Mono<MovementDto> accountOpening(MovementDto movementDto) {
        return Mono.justOrEmpty(movementDto)
                .defaultIfEmpty(new MovementDto(new Customer(), new Product()))
                .map(mov -> mov.getCustomer())
                .flatMap(customerService::checkCustomerExistsElseCreate)
                .doOnNext(cus1 -> movementDto.getProduct().setCustomerId(cus1.getId()))
                .flatMap(cus1 -> productService.validateCustomerCanProduct(movementDto))
                .defaultIfEmpty(new Product(""))
                .flatMap(pro1 -> (pro1.getId() == null || pro1.getId().isEmpty()) ? productService.insertProduct(pro1) : Mono.just(pro1))
                .flatMap(product -> {
                    Customer customer = movementDto.getCustomer();
                    if(product.getId() == null || product.getId().isEmpty() || product.getId().equals("") || customer.getId() == null || customer.getId().isEmpty() || customer.getId().equals("")){
                        return Mono.empty();
                    }else{
                        movementDto.setId(null);
                        movementDto.setDate(new Date());
                        movementDto.setConcept(MovementMongo.MOVEMENT_CONCEPT_1);
                        movementDto.setType(MovementMongo.MOVEMENT_TYPE_1);
                        movementDto.setProduct(product);
                        return movementRepository.insert(MovementDto.transformIntoMongo(movementDto));
                    }
                })
                .map(mov -> {
                    MovementDto movement = MovementDto.transformIntoDto(mov);
                    BeanUtils.copyProperties(movementDto.getProduct(), movement.getProduct(), "id");
                    BeanUtils.copyProperties(movementDto.getCustomer(), movement.getCustomer(), "id");
                    return movement;
                });
        //SI EL OJB MOVEMENTMONGO ES NULL O SU ID ES NULL DEBERIA REVERTIRSE EL REGISTRO DEL CLIENTE Y DEL PRODUCTO
    }

    @Override
    public Mono<MovementDto> registerWithdrawal(MovementDto movementDto) {
        return null;
    }

    @Override
    public Mono<MovementDto> registerDeposit(MovementDto movementDto) {
        return null;
    }

    @Override
    public Mono<MovementDto> registerPayment(MovementDto movementDto) {
        return null;
    }

    @Override
    public Mono<MovementDto> registerSpent(MovementDto movementDto) {
        return null;
    }

}
