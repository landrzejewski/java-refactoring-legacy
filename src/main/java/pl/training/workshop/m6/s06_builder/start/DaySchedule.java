package pl.training.workshop.m6.s06_builder.start;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Start - korzeń drzewa: dzień z listą sal. */
public final class DaySchedule {
    private final LocalDate date;
    private final List<Hall> halls = new ArrayList<>();

    public DaySchedule(LocalDate date) {
        this.date = date;
    }

    public void add(Hall hall) {
        halls.add(hall);
    }

    public String render() {
        StringBuilder text = new StringBuilder()
                .append(date).append(' ').append(date.getDayOfWeek()).append('\n');
        int count = 0;
        for (Hall hall : halls) {
            text.append(hall.render());
            count += hall.size();
        }
        return text.append("Seansow: ").append(count).append('\n').toString();
    }
}
