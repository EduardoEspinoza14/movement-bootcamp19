package com.nttdata.movement.controller;

import com.nttdata.movement.bussiness.ProductService;
import com.nttdata.movement.model.dto.Employee;
import com.nttdata.movement.model.dto.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class ProductController.
 */
@RestController
@RequestMapping("/products/{customerId}")
@CrossOrigin(origins = "*", methods = {
    RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE
})
public class ProductController {

  private final Logger log = LoggerFactory.getLogger(EmployeeController.class);

  @Autowired
  ProductService service;

  @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<Product> getProductsByCustomer(@PathVariable String customerId) {
    return service.getProductsByCustomer(customerId);
  }

  @GetMapping(value = "/available-balance/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Double> availableBalance(@PathVariable String customerId,
                                       @PathVariable String productId) {
    return service.getAvailableBalance(customerId, productId);
  }

}
