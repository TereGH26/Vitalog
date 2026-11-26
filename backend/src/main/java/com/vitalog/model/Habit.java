package com.vitalog.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document(collection = "habits")
public record Habit(
        @Id String id,
        String accountEmail,
        String name,
        int durationDays,
        String frequency,
        Set<String> weekdays,
        Instant startDate,
        String status) {
}
