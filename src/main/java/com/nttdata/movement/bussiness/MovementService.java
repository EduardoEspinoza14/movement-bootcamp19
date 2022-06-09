package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.dto.MovementDto;
import com.nttdata.movement.model.dto.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementService {

    Flux<MovementDto> listMovements(String customerId, String productId);

    Mono<MovementDto> accountOpening(MovementDto movementDto);

    Mono<MovementDto> registerWithdrawal(MovementDto movementDto);

    Mono<MovementDto> registerDeposit(MovementDto movementDto);

    Mono<MovementDto> registerPayment(MovementDto movementDto);

    Mono<MovementDto> registerSpent(MovementDto movementDto);

}
