package com.pochemuto.homealone.marafon;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends MongoRepository<Meal, Meal.Key> {
    List<Meal> findById_WeekAndId_Day(int week, DayOfWeek day);
}
