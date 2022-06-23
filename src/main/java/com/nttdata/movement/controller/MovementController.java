package com.nttdata.movement.controller;

import com.nttdata.movement.bussiness.MovementService;
import com.nttdata.movement.model.mongo.MovementMongo;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class MovementController.
 */
@RestController
@RequestMapping("/movement")
@CrossOrigin(origins = "*", methods = {
    RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
public class MovementController {

  private final Logger log = LoggerFactory.getLogger(MovementController.class);

  @Autowired
  MovementService service;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<MovementMongo> getAllMovements() {
    return service.getMovements();
  }

  @GetMapping(value = "/product/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<MovementMongo> getAllMovements(@PathVariable String productId) {
    return service.getMovementsProduct(productId);
  }

  @GetMapping(value = "/count/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Long> getCountMovementsProduct(@PathVariable String productId,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                             LocalDateTime start,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                             LocalDateTime end) {
    log.info("COUNT MOVEMENTS OF {} BETWEEN {} AND {}", productId, start, end);
    return service.countMovementsByProductDate(productId, start, end);
  }

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<MovementMongo> getMovement(@PathVariable String id) {
    return service.getMovement(id);
  }

  @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<MovementMongo> createMovement(@RequestBody MovementMongo movement) {
    return service.insertMovement(movement);
  }

  @PostMapping(value = "/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<MovementMongo> modifyMovement(@RequestBody MovementMongo movement,
                                            @PathVariable String id) {
    return service.updateMovement(movement, id);
  }

  @PostMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Void> removeMovement(@PathVariable String id) {
    return service.deleteMovement(id);
  }

}
