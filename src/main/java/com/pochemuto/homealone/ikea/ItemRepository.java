package com.pochemuto.homealone.ikea;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends MongoRepository<Item, Integer> {
    List<Item> findByRemovedFalse();
}
