package pl.training.workshop.m7.s05_breakmethod.step2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.training.workshop.m7.s05_breakmethod.Screening;

/**
 * Krok 2: Extract Method dla etapu porządkowania - filtrowanie odwołanych i sortowanie
 * w order(). Sortujemy kopię, więc lista klienta nadal pozostaje nietknięta.
 */
public final class RepertoireBuilder {
    public String build(List<Screening> screenings) {
        List<Screening> copy = validateAndCopy(screenings);
        List<Screening> active = order(copy);
        StringBuilder text = new StringBuilder("REPERTUAR\n");
        for (Screening screening : active) {
            text.append(screening.start()).append(' ').append(screening.title())
                    .append(" (").append(screening.format()).append("), sala ")
                    .append(screening.hall()).append('\n');
        }
        if (active.isEmpty()) {
            text.append("brak seansow\n");
        }
        return text.toString();
    }

    private List<Screening> validateAndCopy(List<Screening> screenings) {
        List<Screening> copy = new ArrayList<>();
        for (Screening screening : screenings) {
            if (screening == null) {
                throw new IllegalArgumentException("screening must not be null");
            }
            copy.add(screening);
        }
        return copy;
    }

    private List<Screening> order(List<Screening> screenings) {
        List<Screening> active = new ArrayList<>();
        for (Screening screening : screenings) {
            if (!screening.cancelled()) {
                active.add(screening);
            }
        }
        active.sort(Comparator.comparing(Screening::start).thenComparing(Screening::title));
        return active;
    }
}
