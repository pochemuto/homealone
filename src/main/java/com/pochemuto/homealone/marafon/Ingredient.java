package com.pochemuto.homealone.marafon;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Ingredient {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;
}
