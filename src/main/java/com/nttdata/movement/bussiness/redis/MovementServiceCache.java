package com.nttdata.movement.bussiness.redis;

import com.nttdata.movement.bussiness.impl.MovementServiceImpl;
import com.nttdata.movement.model.mongo.MovementMongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Class CustomerServiceCache.
 */
@Service
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class MovementServiceCache extends MovementServiceImpl {

  private final Logger log = LoggerFactory.getLogger(MovementServiceCache.class);

  private static final String KEY_CACHE = "movements";

  @Autowired
  private ReactiveHashOperations<String, String, MovementMongo> hashOperations;

  @Override
  public Mono<MovementMongo> getMovement(String id) {
    return hashOperations.get(KEY_CACHE, id)
            .switchIfEmpty(this.getCustomerSaveCacheRedis(id));
  }

  @Override
  public Mono<MovementMongo> insertMovement(MovementMongo movementMongo) {
    return super.insertMovement(movementMongo)
            .flatMap(this::saveCacheRedis);
  }

  @Override
  public Mono<MovementMongo> updateMovement(MovementMongo movementMongo, String id) {
    return this.hashOperations.remove(KEY_CACHE, id)
            .then(super.updateMovement(movementMongo, id))
            .flatMap(this::saveCacheRedis);
  }

  @Override
  public Mono<Void> deleteMovement(String id) {
    return this.hashOperations.remove(KEY_CACHE, id)
            .then(super.deleteMovement(id));
  }

  private Mono<MovementMongo> getCustomerSaveCacheRedis(String id) {
    return super.getMovement(id)
            .flatMap(this::saveCacheRedis);
  }

  private Mono<MovementMongo> saveCacheRedis(MovementMongo movementMongo) {
    log.info("REDIS CACHE MOVEMENT: {}", movementMongo);
    return this.hashOperations.put(KEY_CACHE,
                    movementMongo.getId(),
                    movementMongo)
            .thenReturn(movementMongo);
  }

}
