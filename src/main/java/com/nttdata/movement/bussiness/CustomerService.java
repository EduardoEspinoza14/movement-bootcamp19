package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.Customer;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Mono<Customer> getCustomerById(String id);

    Mono<Customer> insertCustomer(Customer customer);

    Mono<Customer> checkCustomerExistsElseCreate(Customer customer);

}
