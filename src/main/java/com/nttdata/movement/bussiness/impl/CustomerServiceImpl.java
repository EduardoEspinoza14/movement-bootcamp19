package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.CustomerService;
import com.nttdata.movement.model.dto.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Value("${api.customer.baseUri}")
    private String baseUri;

    @Value("${api.customer.personUri}")
    private String personUri;

    @Value("${api.customer.companyUri}")
    private String companyUri;

    @Autowired
    WebClient.Builder webClientBuilder;

    @Override
    public Mono<Customer> getCustomerById(String id) {
        if(id == null || id.isEmpty() || id.trim().equals("")){
            return Mono.empty();
        }
        return webClientBuilder.build()
                .get()
                .uri(baseUri + "/{customerId}", id)
                .retrieve()
                .bodyToMono(Customer.class);
    }

    @Override
    public Mono<Customer> insertCustomer(Customer customer) {
        String uri;
        customer.setId(null);
        if(customer.getType() == null){ return Mono.empty(); }
        if(customer.getType().equals(Customer.CUSTOMER_TYPE_1)){
            uri = personUri;
        }else if(customer.getType().equals(Customer.CUSTOMER_TYPE_2)){
            uri = companyUri;
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

    @Override
    public Mono<Customer> checkCustomerExistsElseCreate(Customer customer){
        return Mono.just(customer)
                .defaultIfEmpty(new Customer(""))
                .flatMap(cus -> this.getCustomerById(cus.getId()))
                .defaultIfEmpty(new Customer(""))
                .doOnNext(cus1-> {
                    if(cus1.getId() == null || cus1.getId().isEmpty()){
                        BeanUtils.copyProperties(customer, cus1, "id");
                    }else{
                        BeanUtils.copyProperties(cus1, customer, "id");
                    }
                })
                .flatMap(cus1 -> (cus1.getId() == null || cus1.getId().isEmpty()) ? this.insertCustomer(cus1) : Mono.just(cus1));
    }

}
