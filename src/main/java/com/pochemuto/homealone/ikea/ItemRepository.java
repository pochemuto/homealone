package com.pochemuto.homealone.ikea;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    @Query("SELECT DISTINCT i.name FROM Item i")
    Set<String> findNames();
}
