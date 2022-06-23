package com.nttdata.movement.bussiness;

import com.nttdata.movement.model.mongo.MovementMongo;
import java.time.LocalDateTime;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface MovementService.
 */
public interface MovementService {

  Flux<MovementMongo> getMovements();

  //PARA LISTAR LOS MOVIMIENTOS DE UN PRODUCTO POR ID DE PRODUCTO Y CLIENTE
  Flux<MovementMongo> getMovementsProduct(String productId);

  Mono<MovementMongo> getMovement(String id);

  Mono<MovementMongo> insertMovement(MovementMongo customer);

  Mono<MovementMongo> updateMovement(MovementMongo customer, String id);

  Mono<Void> deleteMovement(String id);

  Mono<Long> countMovementsByProductDate(String productId, LocalDateTime start, LocalDateTime end);

}
