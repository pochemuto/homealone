package com.pochemuto.homealone.marafon;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Meal {

    @Id
    private Key id;
    private String title;
    private String cooking;

    private List<Ingredient> ingredients;

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Key implements Serializable {
        private int week;
        private DayOfWeek day;
        private int num;
    }
}
