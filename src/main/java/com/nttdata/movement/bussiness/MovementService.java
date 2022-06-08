package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import reactor.core.publisher.Mono;

public interface MovementService {

    Mono<Product> createProductCustomer(MovementDto movementDto);

}
