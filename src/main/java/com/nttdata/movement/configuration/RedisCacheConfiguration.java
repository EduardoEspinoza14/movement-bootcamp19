package com.nttdata.movement.configuration;

import com.nttdata.movement.model.mongo.MovementMongo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Class RedisCacheConfiguration.
 */
@Configuration
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class RedisCacheConfiguration {

  /**
   * Method hashOperations.
   */
  @Bean
  public ReactiveHashOperations<String, String, MovementMongo> hashOperations(
          ReactiveRedisConnectionFactory redisConnectionFactory) {
    return new ReactiveRedisTemplate<>(
            redisConnectionFactory,
            RedisSerializationContext
                    .<String, MovementMongo>newSerializationContext(new StringRedisSerializer())
                    .hashKey(new GenericToStringSerializer<>(String.class))
                    .hashValue(new Jackson2JsonRedisSerializer<>(MovementMongo.class))
                    .build()
    ).opsForHash();
  }

}
