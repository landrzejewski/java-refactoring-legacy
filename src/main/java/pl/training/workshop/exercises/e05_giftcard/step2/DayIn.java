package pl.training.workshop.exercises.e05_giftcard.step2;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts;

/** Krok 2: wyrażenie terminalne - dzień seansu należy do listy (w kolejności z reguły). */
public record DayIn(List<DayOfWeek> days) implements VoucherRule {
    public DayIn {
        days = List.copyOf(days);
    }

    @Override
    public boolean applies(ScreeningFacts facts) {
        return days.contains(facts.day());
    }

    @Override
    public String describe() {
        return "dzień: " + days.stream()
                .map(day -> day.name().substring(0, 3))
                .collect(Collectors.joining(", "));
    }
}
