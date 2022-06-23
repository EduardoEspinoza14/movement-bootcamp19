package com.nttdata.movement.bussiness.impl;

import com.nttdata.movement.bussiness.MovementService;
import com.nttdata.movement.configuration.KafkaProducerConfiguration;
import com.nttdata.movement.model.mongo.MovementMongo;
import com.nttdata.movement.model.repository.MovementRepository;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderOptions;

/**
 * Class MovementServiceImpl.
 */
@Service
@ConditionalOnProperty(name = "cache.enabled", havingValue = "false", matchIfMissing = true)
public class MovementServiceImpl implements MovementService {

  @Autowired
  private MovementRepository movementRepository;

  @Autowired
  private SenderOptions<String, MovementMongo> senderOptions;

  @Override
  public Flux<MovementMongo> getMovements() {
    return movementRepository.findAll();
  }

  @Override
  public Flux<MovementMongo> getMovementsProduct(String productId) {
    return movementRepository.findByProductIdOrderByDateDesc(productId);
  }

  @Override
  public Mono<MovementMongo> getMovement(String id) {
    return movementRepository.findById(id);
  }

  @Override
  public Mono<MovementMongo> insertMovement(MovementMongo movement) {
    movement.setId(null);
    movement.setDate(new Date());
    return movementRepository.insert(movement)
            .doOnSuccess(movementMongo -> KafkaProducerConfiguration
                    .senderCreate(senderOptions,
                            KafkaProducerConfiguration.insertRecord(movementMongo))
                    .subscribe());
  }

  @Override
  public Mono<MovementMongo> updateMovement(MovementMongo movement, String id) {
    return movementRepository.findById(id)
            .map(movementMongo -> {
              BeanUtils.copyProperties(movement, movementMongo, "id", "type", "concept");
              return movementMongo;
            })
            .flatMap(movementRepository::save)
            .doOnSuccess(movementMongo -> KafkaProducerConfiguration
                    .senderCreate(senderOptions,
                            KafkaProducerConfiguration.updateRecord(movementMongo))
                    .subscribe());
  }

  @Override
  public Mono<Void> deleteMovement(String id) {
    return movementRepository.findById(id)
            .flatMap(c -> movementRepository.deleteById(c.getId()))
            .doOnSuccess(voidReturn -> KafkaProducerConfiguration
                    .senderCreate(senderOptions, KafkaProducerConfiguration.deleteRecord(id))
                    .subscribe());
  }

  @Override
  public Mono<Long> countMovementsByProductDate(String productId,
                                                LocalDateTime start,
                                                LocalDateTime end) {
    return movementRepository.countByProductIdAndDateBetween(productId, start, end);
  }

}
