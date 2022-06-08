package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.Customer;
import com.nttdata.movement.model.dto.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    public Flux<Product> getProductsByCustomer(String customerId);

    public Mono<Product> insertProduct(Product product);

}
