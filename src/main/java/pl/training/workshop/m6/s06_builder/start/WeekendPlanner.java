package pl.training.workshop.m6.s06_builder.start;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Start: klient buduje drzewo dzień - sala - seans ręcznie. Dużo new/add, łatwo zapomnieć
 * day.add(hall), a kod nie przypomina kształtu repertuaru.
 */
public final class WeekendPlanner {
    public DaySchedule plan(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean weekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        DaySchedule day = new DaySchedule(date);
        Hall hall1 = new Hall("Sala 1");
        hall1.add(new Screening("Diuna", LocalTime.of(18, 0)));
        hall1.add(new Screening("Diuna", LocalTime.of(21, 0)));
        day.add(hall1);
        Hall hall2 = new Hall("Sala 2");
        if (weekend) {
            hall2.add(new Screening("Kraina Lodu", LocalTime.of(10, 0)));
        }
        hall2.add(new Screening("Amator", LocalTime.of(17, 30)));
        day.add(hall2);
        Hall hall3 = new Hall("Sala 3 VIP");
        if (weekend) {
            hall3.add(new Screening("Amator", LocalTime.of(20, 0)));
        }
        day.add(hall3);
        return day;
    }
}
