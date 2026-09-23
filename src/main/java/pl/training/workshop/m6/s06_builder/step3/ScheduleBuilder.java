package pl.training.workshop.m6.s06_builder.step3;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Krok 3: zagnieżdżony builder - sala konfigurowana lambdą, więc znika ukryty stan
 * "bieżąca sala". Lambda jest wywoływana synchronicznie, dokładnie raz.
 */
public final class ScheduleBuilder {
    private final LocalDate date;
    private final List<Hall> halls = new ArrayList<>();
    private boolean built;

    private ScheduleBuilder(LocalDate date) {
        this.date = date;
    }

    public static ScheduleBuilder day(LocalDate date) {
        return new ScheduleBuilder(date);
    }

    public ScheduleBuilder hall(String name, Consumer<HallBuilder> content) {
        requireNotBuilt();
        if (halls.stream().anyMatch(hall -> hall.name().equals(name))) {
            throw new IllegalStateException("duplicate hall: " + name);
        }
        HallBuilder hall = new HallBuilder();
        content.accept(hall);
        halls.add(new Hall(name, hall.screenings));
        return this;
    }

    public DaySchedule build() {
        requireNotBuilt();
        built = true;
        return new DaySchedule(date, halls);
    }

    private void requireNotBuilt() {
        if (built) {
            throw new IllegalStateException("builder already used");
        }
    }

    public static final class HallBuilder {
        private final List<Screening> screenings = new ArrayList<>();

        private HallBuilder() {
        }

        public HallBuilder screening(String title, int hour, int minute) {
            screenings.add(new Screening(title, LocalTime.of(hour, minute)));
            return this;
        }
    }
}
