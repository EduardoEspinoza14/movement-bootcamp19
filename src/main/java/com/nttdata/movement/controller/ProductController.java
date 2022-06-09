package com.nttdata.movement.controller;

import com.nttdata.movement.bussiness.ProductService;
import com.nttdata.movement.model.dto.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    ProductService service;

    @GetMapping(value = "/available-balance/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Double> availableBalance(@PathVariable String customerId, @PathVariable String productId){
        return service.getAvailableBalance(customerId, productId);
    }

}
