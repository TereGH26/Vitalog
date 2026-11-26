package com.vitalog.service;

import com.vitalog.dto.CreateHabitRequest;
import com.vitalog.dto.HabitResponse;
import com.vitalog.model.Habit;
import com.vitalog.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HabitService {
    public static final int DEFAULT_DURATION_DAYS = 66;

    private static final Set<String> ALLOWED_WEEKDAYS = Set.of(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

    private static final Map<String, String> WEEKDAY_ALIASES = Map.ofEntries(
            Map.entry("MONDAY", "MONDAY"), Map.entry("MON", "MONDAY"), Map.entry("LUNES", "MONDAY"),
            Map.entry("TUESDAY", "TUESDAY"), Map.entry("TUE", "TUESDAY"), Map.entry("MARTES", "TUESDAY"),
            Map.entry("WEDNESDAY", "WEDNESDAY"), Map.entry("WED", "WEDNESDAY"), Map.entry("MIERCOLES", "WEDNESDAY"),
            Map.entry("THURSDAY", "THURSDAY"), Map.entry("THU", "THURSDAY"), Map.entry("JUEVES", "THURSDAY"),
            Map.entry("FRIDAY", "FRIDAY"), Map.entry("FRI", "FRIDAY"), Map.entry("VIERNES", "FRIDAY"),
            Map.entry("SATURDAY", "SATURDAY"), Map.entry("SAT", "SATURDAY"), Map.entry("SABADO", "SATURDAY"),
            Map.entry("SUNDAY", "SUNDAY"), Map.entry("SUN", "SUNDAY"), Map.entry("DOMINGO", "SUNDAY"));

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public HabitResponse create(CreateHabitRequest request, String accountEmail) {
        String name = request.name().trim();
        int duration = request.effectiveDurationDays();
        if (duration < 1) {
            throw new InvalidHabitException("La duración debe ser de al menos 1 día.");
        }

        String frequencyInput = request.frequency().trim().toUpperCase(Locale.ROOT);
        String frequency;
        if (frequencyInput.equals("DAILY") || frequencyInput.equals("DIARIA") || frequencyInput.equals("DIARIO")) {
            frequency = "DAILY";
        } else if (frequencyInput.equals("WEEKLY") || frequencyInput.equals("SEMANAL")) {
            frequency = "WEEKLY";
        } else {
            throw new InvalidHabitException("Selecciona una frecuencia válida.");
        }

        Set<String> weekdays = Set.of();
        if (frequency.equals("WEEKLY")) {
            if (request.weekdays() == null || request.weekdays().isEmpty()) {
                throw new InvalidHabitException("Selecciona al menos un día para la frecuencia semanal.");
            }
            weekdays = request.weekdays().stream()
                    .filter(java.util.Objects::nonNull)
                    .map(day -> day.trim().toUpperCase(Locale.ROOT).replace("É", "E").replace("Á", "A").replace("Í", "I").replace("Ó", "O").replace("Ú", "U"))
                    .map(WEEKDAY_ALIASES::get)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());
            if (weekdays.isEmpty() || !ALLOWED_WEEKDAYS.containsAll(weekdays) ||
                    weekdays.size() != request.weekdays().size()) {
                throw new InvalidHabitException("Selecciona días de la semana válidos.");
            }
        }

        Habit habit = new Habit(
                null,
                accountEmail,
                name,
                duration,
                frequency,
                weekdays,
                Instant.now(),
                "IN_CONSTRUCTION");

        return toResponse(habitRepository.save(habit));
    }

    public java.util.List<HabitResponse> findByAccount(String accountEmail) {
        return habitRepository.findByAccountEmailOrderByStartDateDesc(accountEmail).stream()
                .map(this::toResponse)
                .toList();
    }

    private HabitResponse toResponse(Habit habit) {
        return new HabitResponse(habit.id(), habit.name(), habit.durationDays(), habit.frequency(),
                habit.weekdays(), habit.startDate(), habit.status());
    }
}
