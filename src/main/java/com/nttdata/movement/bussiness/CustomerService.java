package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.Customer;
import reactor.core.publisher.Mono;

public interface CustomerService {

    public Mono<Customer> getCustomerById(String id);

    public Mono<Customer> insertCustomer(Customer customer);

}
