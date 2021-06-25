package com.pochemuto.homealone.strida;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Bike(
        @Id Integer id,
        String title,
        String description,
        int price,
        String availability
) {
}
