package com.nttdata.movement.controller;

import com.nttdata.movement.bussiness.MovementService;
import com.nttdata.movement.model.dto.MovementDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movement")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class MovementController {

    private final Logger log = LoggerFactory.getLogger(MovementController.class);

    @Autowired
    MovementService service;

    @GetMapping(value = "/list-movements/{customerId}/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<MovementDto> listMovements(@PathVariable String customerId, @PathVariable String productId){
        return service.listMovements(customerId, productId);
    }

    @PostMapping(value = "/account-opening", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MovementDto> accountOpening(@RequestBody MovementDto movementDto){
        return service.accountOpening(movementDto);
    }

    @PostMapping(value = "/register-withdrawal", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MovementDto> registerWithdrawal(@RequestBody MovementDto movementDto){
        return service.registerWithdrawal(movementDto);
    }

    @PostMapping(value = "/register-deposit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MovementDto> registerDeposit(@RequestBody MovementDto movementDto){
        return service.registerDeposit(movementDto);
    }

    @PostMapping(value = "/register-payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MovementDto> registerPayment(@RequestBody MovementDto movementDto){
        return service.registerPayment(movementDto);
    }

    @PostMapping(value = "/register-spent", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MovementDto> registerSpent(@RequestBody MovementDto movementDto){
        return service.registerSpent(movementDto);
    }

}
