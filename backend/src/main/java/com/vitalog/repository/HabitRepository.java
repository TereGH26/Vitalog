package com.vitalog.repository;

import com.vitalog.model.Habit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HabitRepository extends MongoRepository<Habit, String> {
    List<Habit> findByAccountEmailOrderByStartDateDesc(String accountEmail);
}
