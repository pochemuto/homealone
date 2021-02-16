package com.pochemuto.homealone.marafon;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
@IdClass(Meal.ID.class)
public class Meal {

    @Id
    private int week;
    @Id
    private DayOfWeek day;
    @Id
    private int num;

    private String title;
    private String cooking;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    private List<Ingredient> ingredients;

    @Data
    public static class ID implements Serializable {
        @Id
        private int week;
        @Id
        private DayOfWeek day;
        @Id
        private int num;
    }
}
