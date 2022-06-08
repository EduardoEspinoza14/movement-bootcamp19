package com.nttdata.movement.controller;

import com.nttdata.movement.bussiness.MovementService;
import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movement")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class MovementController {

    private final Logger log = LoggerFactory.getLogger(MovementController.class);

    @Autowired
    MovementService service;

    @PostMapping(value = "/create-product", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Product> createProduct(@RequestBody MovementDto movementDto){
        return service.createProduct(movementDto);
    }

}
