package com.vitalog.service;

import com.vitalog.dto.CreateHabitRequest;
import com.vitalog.dto.HabitResponse;
import com.vitalog.model.Habit;
import com.vitalog.repository.HabitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitServiceTests {

    @Mock
    private HabitRepository habitRepository;

    @Test
    void createsDailyHabitWithDefaultDurationAndAuthenticatedAccount() {
        when(habitRepository.save(any(Habit.class))).thenAnswer(invocation -> {
            Habit habit = invocation.getArgument(0);
            return new Habit("habit-1", habit.accountEmail(), habit.name(), habit.durationDays(),
                    habit.frequency(), habit.weekdays(), habit.startDate(), habit.status());
        });

        HabitService service = new HabitService(habitRepository);
        HabitResponse result = service.create(new CreateHabitRequest("Beber agua", null, "DAILY", null), "ana@example.com");

        assertThat(result.durationDays()).isEqualTo(66);
        assertThat(result.frequency()).isEqualTo("DAILY");
        assertThat(result.weekdays()).isEmpty();
        assertThat(result.status()).isEqualTo("IN_CONSTRUCTION");
        assertThat(result.startDate()).isNotNull();
        verify(habitRepository).save(any(Habit.class));
    }

    @Test
    void createsWeeklyHabitWithSelectedDays() {
        when(habitRepository.save(any(Habit.class))).thenAnswer(invocation -> {
            Habit habit = invocation.getArgument(0);
            return new Habit("habit-2", habit.accountEmail(), habit.name(), habit.durationDays(),
                    habit.frequency(), habit.weekdays(), habit.startDate(), habit.status());
        });

        HabitService service = new HabitService(habitRepository);
        HabitResponse result = service.create(
                new CreateHabitRequest("Correr", 30, "WEEKLY", Set.of("MONDAY", "FRIDAY")),
                "ana@example.com");

        assertThat(result.weekdays()).containsExactlyInAnyOrder("MONDAY", "FRIDAY");
    }

    @Test
    void rejectsWeeklyHabitWithoutDays() {
        HabitService service = new HabitService(habitRepository);

        assertThatThrownBy(() -> service.create(
                new CreateHabitRequest("Correr", 66, "WEEKLY", Set.of()), "ana@example.com"))
                .isInstanceOf(InvalidHabitException.class)
                .hasMessage("Selecciona al menos un día para la frecuencia semanal.");
    }

    @Test
    void rejectsDurationBelowOneDay() {
        HabitService service = new HabitService(habitRepository);

        assertThatThrownBy(() -> service.create(
                new CreateHabitRequest("Leer", 0, "DAILY", null), "ana@example.com"))
                .isInstanceOf(InvalidHabitException.class)
                .hasMessage("La duración debe ser de al menos 1 día.");
    }
}
