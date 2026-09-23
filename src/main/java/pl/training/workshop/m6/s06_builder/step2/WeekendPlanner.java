package pl.training.workshop.m6.s06_builder.step2;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Krok 2: klient bez zmian - zmieniła się tylko reprezentacja drzewa pod builderem. */
public final class WeekendPlanner {
    public DaySchedule plan(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean weekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        ScheduleBuilder builder = new ScheduleBuilder(date)
                .hall("Sala 1")
                .screening("Diuna", 18, 0)
                .screening("Diuna", 21, 0)
                .hall("Sala 2");
        if (weekend) {
            builder.screening("Kraina Lodu", 10, 0);
        }
        builder.screening("Amator", 17, 30)
                .hall("Sala 3 VIP");
        if (weekend) {
            builder.screening("Amator", 20, 0);
        }
        return builder.build();
    }
}
