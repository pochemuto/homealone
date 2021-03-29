package com.pochemuto.homealone.ikea;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ikea")
public class Item {
    @Id
    int id;

    String name;

    BigDecimal price;

    boolean reduced;

    boolean removed;

    Instant lastSeen;
}
