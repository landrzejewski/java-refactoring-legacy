package pl.training.workshop.m7.s05_breakmethod.step1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.training.workshop.m7.s05_breakmethod.Screening;

/**
 * Krok 1: Extract Method dla fragmentu z jednym wejściem i jednym wynikiem -
 * kontrola wejścia i kopia przeniesione dosłownie do validateAndCopy().
 */
public final class RepertoireBuilder {
    public String build(List<Screening> screenings) {
        List<Screening> copy = validateAndCopy(screenings);
        List<Screening> active = new ArrayList<>();
        for (Screening screening : copy) {
            if (!screening.cancelled()) {
                active.add(screening);
            }
        }
        active.sort(Comparator.comparing(Screening::start).thenComparing(Screening::title));
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
}
