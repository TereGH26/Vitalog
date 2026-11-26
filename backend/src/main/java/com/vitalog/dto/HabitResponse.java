package com.vitalog.dto;

import java.time.Instant;
import java.util.Set;

public record HabitResponse(
        String id,
        String name,
        int durationDays,
        String frequency,
        Set<String> weekdays,
        Instant startDate,
        String status) {
}
