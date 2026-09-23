package pl.training.workshop.m6.s06_builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s06_builder.step3.DaySchedule;
import pl.training.workshop.m6.s06_builder.step3.ScheduleBuilder;

/** Niezmienniki buildera: jednorazowość, unikalne sale, niemutowalny wynik. */
final class S06SolutionTest {
    private static final LocalDate DAY = LocalDate.of(2026, 10, 3);

    @Test
    void builderCannotBeReused() {
        ScheduleBuilder builder = ScheduleBuilder.day(DAY).hall("Sala 1", hall -> hall.screening("Diuna", 18, 0));
        builder.build();
        assertThrows(IllegalStateException.class, builder::build);
        assertThrows(IllegalStateException.class, () -> builder.hall("Sala 2", hall -> { }));
    }

    @Test
    void hallNamesMustBeUnique() {
        var error = assertThrows(IllegalStateException.class, () -> ScheduleBuilder.day(DAY)
                .hall("Sala 1", hall -> { })
                .hall("Sala 1", hall -> { }));
        assertEquals("duplicate hall: Sala 1", error.getMessage());
    }

    @Test
    void builtTreeIsImmutable() {
        DaySchedule day = ScheduleBuilder.day(DAY).hall("Sala 1", hall -> hall.screening("Diuna", 18, 0)).build();
        assertThrows(UnsupportedOperationException.class, () -> day.halls().clear());
        assertThrows(UnsupportedOperationException.class, () -> day.halls().getFirst().screenings().clear());
    }

    @Test
    void classicBuilderRejectsScreeningBeforeHall() {
        var builder = new pl.training.workshop.m6.s06_builder.step1.ScheduleBuilder(DAY);
        assertThrows(IllegalStateException.class, () -> builder.screening("Diuna", 18, 0));
    }
}
