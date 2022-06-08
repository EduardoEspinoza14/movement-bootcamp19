package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.CustomerService;
import com.nttdata.movement.bussiness.MovementService;
import com.nttdata.movement.bussiness.ProductService;
import com.nttdata.movement.model.dto.Customer;
import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class MovementServiceImpl implements MovementService {

    @Autowired
    CustomerService customerService;

    @Autowired
    ProductService productService;

    @Override
    public Mono<Product> createProduct(MovementDto movementDto) {
        return Mono.justOrEmpty(movementDto)
                .defaultIfEmpty(new MovementDto(new Customer(), new Product()))
                /*.doOnNext(mov1 -> mov1.getCustomer().setId(
                        Optional.ofNullable(mov1.getCustomer())
                            .map(cus1 -> cus1.getId())
                                .orElse("")
                        )
                )*/
                .flatMap(mov1 -> customerService.getCustomerById(mov1.getCustomer().getId()))
                        .defaultIfEmpty(new Customer(""))
                        .doOnNext(cus1-> {
                            if(cus1.getId() == null || cus1.getId().isEmpty()){
                                BeanUtils.copyProperties(movementDto.getCustomer(), cus1, "id");
                            }
                        })
                .flatMap(cus1 -> (cus1.getId() == null || cus1.getId().isEmpty()) ? customerService.insertCustomer(cus1) : Mono.just(cus1))
                        .defaultIfEmpty(new Customer(""))
                        .doOnNext(cus1 -> movementDto.getProduct().setCustomerId(cus1.getId()))
                .map(cus1 -> movementDto.getProduct())
                        .defaultIfEmpty(new Product(""))
                .flatMap(pro1 -> (pro1.getId() == null || pro1.getId().isEmpty()) ? productService.insertProduct(pro1) : Mono.just(pro1));
    }

}
