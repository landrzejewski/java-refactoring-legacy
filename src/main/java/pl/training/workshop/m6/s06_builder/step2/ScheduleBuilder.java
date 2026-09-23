package pl.training.workshop.m6.s06_builder.step2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Krok 2: builder zbiera dane, a drzewo (rekordy) powstaje w build(). Builder jest jednorazowy -
 * drugie build() rzuca wyjątek, bo wynik nie może zależeć od późniejszych wywołań.
 */
public final class ScheduleBuilder {
    private final LocalDate date;
    private final Map<String, List<Screening>> halls = new LinkedHashMap<>();
    private List<Screening> current;
    private boolean built;

    public ScheduleBuilder(LocalDate date) {
        this.date = date;
    }

    public ScheduleBuilder hall(String name) {
        requireNotBuilt();
        if (halls.containsKey(name)) {
            throw new IllegalStateException("duplicate hall: " + name);
        }
        current = new ArrayList<>();
        halls.put(name, current);
        return this;
    }

    public ScheduleBuilder screening(String title, int hour, int minute) {
        requireNotBuilt();
        if (current == null) {
            throw new IllegalStateException("screening without hall");
        }
        current.add(new Screening(title, LocalTime.of(hour, minute)));
        return this;
    }

    public DaySchedule build() {
        requireNotBuilt();
        built = true;
        List<Hall> result = new ArrayList<>();
        halls.forEach((name, screenings) -> result.add(new Hall(name, screenings)));
        return new DaySchedule(date, result);
    }

    private void requireNotBuilt() {
        if (built) {
            throw new IllegalStateException("builder already used");
        }
    }
}
