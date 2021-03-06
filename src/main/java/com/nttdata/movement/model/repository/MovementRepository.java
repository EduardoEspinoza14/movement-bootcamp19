package com.nttdata.movement.model.repository;

import com.nttdata.movement.model.mongo.MovementMongo;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface MovementRepository.
 */
@Repository
public interface MovementRepository extends ReactiveMongoRepository<MovementMongo, String> {

  Flux<MovementMongo> findByProductId(String productId);

  Flux<MovementMongo> findByProductIdOrderByDateDesc(String productId);

  Mono<Long> countByProductIdAndDateBetween(String productId, LocalDateTime from, LocalDateTime to);

}
