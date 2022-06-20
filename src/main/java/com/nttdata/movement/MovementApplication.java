package com.nttdata.movement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * Class MovementApplication.
 */
@SpringBootApplication
@EnableReactiveMongoRepositories
public class MovementApplication {

  public static void main(String[] args) {
    SpringApplication.run(MovementApplication.class, args);
  }

}
