package pl.training.workshop.m6.s06_builder.step2;

import java.time.LocalDate;
import java.util.List;

/** Krok 2: niemutowalny korzeń - builder jest jedyną wygodną drogą budowy. */
public record DaySchedule(LocalDate date, List<Hall> halls) {
    public DaySchedule {
        halls = List.copyOf(halls);
    }

    public String render() {
        StringBuilder text = new StringBuilder()
                .append(date).append(' ').append(date.getDayOfWeek()).append('\n');
        int count = 0;
        for (Hall hall : halls) {
            text.append(hall.render());
            count += hall.screenings().size();
        }
        return text.append("Seansow: ").append(count).append('\n').toString();
    }
}
