package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.Customer;
import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<Product> getProductsByCustomer(String customerId);

    Mono<Product> insertProduct(Product product);

    Mono<Product> validateCustomerCanProduct(MovementDto movementDto);

    Mono<Double> getAvailableBalance(String customerId, String productId);

}
