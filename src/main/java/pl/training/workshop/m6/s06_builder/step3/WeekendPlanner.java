package pl.training.workshop.m6.s06_builder.step3;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Krok 3: wcięcia kodu odpowiadają poziomom drzewa dzień - sala - seans. */
public final class WeekendPlanner {
    public DaySchedule plan(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean weekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        return ScheduleBuilder.day(date)
                .hall("Sala 1", hall -> hall
                        .screening("Diuna", 18, 0)
                        .screening("Diuna", 21, 0))
                .hall("Sala 2", hall -> {
                    if (weekend) {
                        hall.screening("Kraina Lodu", 10, 0);
                    }
                    hall.screening("Amator", 17, 30);
                })
                .hall("Sala 3 VIP", hall -> {
                    if (weekend) {
                        hall.screening("Amator", 20, 0);
                    }
                })
                .build();
    }
}
