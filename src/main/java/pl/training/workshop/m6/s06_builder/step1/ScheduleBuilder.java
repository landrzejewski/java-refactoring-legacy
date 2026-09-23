package pl.training.workshop.m6.s06_builder.step1;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Krok 1: klasyczny Builder (Encapsulate Composite with Builder) - pamięta bieżącą salę,
 * więc klient nie operuje węzłami ani add. Drzewo pod spodem bez zmian.
 */
public final class ScheduleBuilder {
    private final DaySchedule day;
    private Hall current;

    public ScheduleBuilder(LocalDate date) {
        this.day = new DaySchedule(date);
    }

    public ScheduleBuilder hall(String name) {
        current = new Hall(name);
        day.add(current);
        return this;
    }

    public ScheduleBuilder screening(String title, int hour, int minute) {
        if (current == null) {
            throw new IllegalStateException("screening without hall");
        }
        current.add(new Screening(title, LocalTime.of(hour, minute)));
        return this;
    }

    public DaySchedule build() {
        return day;
    }
}
