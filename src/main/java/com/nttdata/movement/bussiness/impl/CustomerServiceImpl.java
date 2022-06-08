package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.CustomerService;
import com.nttdata.movement.model.dto.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    WebClient.Builder webClientBuilder;

    @Override
    public Mono<Customer> getCustomerById(String id) {
        if(id == null || id.isEmpty() || id.trim().equals("")){
            return Mono.empty();
        }
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8083/customer/{customerId}", id)
                .retrieve()
                .bodyToMono(Customer.class);
    }

    @Override
    public Mono<Customer> insertCustomer(Customer customer) {
        String uri = "";
        customer.setId(null);
        if(customer.getType().equals(Customer.CUSTOMER_TYPE_1)){
            uri = "http://localhost:8083/person";
        }else if(customer.getType().equals(Customer.CUSTOMER_TYPE_1)){
            uri = "http://localhost:8083/company";
        }else{
            return Mono.empty();
        }
        return webClientBuilder.build()
                .post()
                .uri(uri).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(customer))
                .retrieve()
                .bodyToMono(Customer.class);
    }

}
