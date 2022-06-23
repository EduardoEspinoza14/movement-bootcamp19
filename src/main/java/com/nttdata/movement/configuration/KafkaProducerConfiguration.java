package com.nttdata.movement.configuration;

import com.nttdata.movement.model.mongo.MovementMongo;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

/**
 * Class KafkaProducerConfiguration.
 */
@Configuration
public class KafkaProducerConfiguration {

  public static final String TOPIC_INSERT = "movement.insert";
  public static final String TOPIC_UPDATE = "movement.update";
  public static final String TOPIC_DELETE = "movement.delete";

  @Value(value = "${kafka.bootstrapAddress:}")
  private String bootstrapAddress;

  public static Flux<SenderResult<Long>> senderCreate(
          SenderOptions<String, MovementMongo> options,
          SenderRecord<String, MovementMongo,
                  Long> record) {
    return KafkaSender.create(options).send(Flux.just(record));
  }

  /**
   * Method senderOptions.
   */
  @Bean
  public SenderOptions<String, MovementMongo> senderOptions() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return SenderOptions.create(props);
  }

  private static SenderRecord<String, MovementMongo, Long> senderRecord(
          String id,
          MovementMongo movementMongo,
          String topic,
          Long correlation) {
    return SenderRecord.create(new ProducerRecord<>(topic, id, movementMongo), correlation);
  }

  private static SenderRecord<String, MovementMongo, Long> senderRecord(
          String id,
          MovementMongo movementMongo,
          String topic) {
    return senderRecord(id, movementMongo, topic, new Date().getTime());
  }

  private static SenderRecord<String, MovementMongo, Long> senderRecord(
          MovementMongo movementMongo,
          String topic) {
    return senderRecord(movementMongo.getId(), movementMongo, topic);
  }

  private static SenderRecord<String, MovementMongo, Long> senderRecord(
          String id,
          String topic) {
    return senderRecord(id, null, topic);
  }

  public static SenderRecord<String, MovementMongo, Long> insertRecord(
          MovementMongo movementMongo) {
    return senderRecord(movementMongo, TOPIC_INSERT);
  }

  public static SenderRecord<String, MovementMongo, Long> updateRecord(
          MovementMongo movementMongo) {
    return senderRecord(movementMongo, TOPIC_UPDATE);
  }

  public static SenderRecord<String, MovementMongo, Long> deleteRecord(
          String id) {
    return senderRecord(id, TOPIC_DELETE);
  }

}
