package com.pochemuto.homealone.ikea;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class Item {
    @Id
    String name;

    BigDecimal price;

    boolean reduced;

    Instant lastSeen;
}
