package com.nttdata.movement.model.repository;

import com.nttdata.movement.model.mongo.MovementMongo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementRepository extends ReactiveMongoRepository<MovementMongo, String> {
}
