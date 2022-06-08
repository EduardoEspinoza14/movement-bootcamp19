package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.ProductService;
import com.nttdata.movement.model.dto.Customer;
import com.nttdata.movement.model.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    WebClient.Builder webClientBuilder;

    @Override
    public Flux<Product> getProductsByCustomer(String customerId) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/product/{customerId}", customerId)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> insertProduct(Product product) {
        String uri = "";
        product.setStart_date(new Date());
        if(product.getType().equals(Product.PRODUCT_TYPE_1)){
            uri = "http://localhost:8082/savings-account";
        }else if(product.getType().equals(Product.PRODUCT_TYPE_2)){
            uri = "http://localhost:8082/checking-account";
        }else if(product.getType().equals(Product.PRODUCT_TYPE_3)){
            uri = "http://localhost:8082/fixed-term";
        }else if(product.getType().equals(Product.PRODUCT_TYPE_4)){
            uri = "http://localhost:8082/card";
        }else if(product.getType().equals(Product.PRODUCT_TYPE_5)){
            uri = "http://localhost:8082/loan";
        }else{
            return Mono.empty();
        }
        return webClientBuilder.build()
                .post()
                .uri(uri).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(product))
                .retrieve()
                .bodyToMono(Product.class);
    }

}
