package com.pochemuto.homealone.strida;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StridaItemRepository extends MongoRepository<Bike, Integer> {
}
