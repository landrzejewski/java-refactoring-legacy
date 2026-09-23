package pl.training.workshop.m7.s05_breakmethod.step3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pl.training.workshop.m7.s05_breakmethod.Screening;

/**
 * Krok 3 (rozwiązanie): Extract Method dla renderowania i Inline Variable.
 * build() opisuje algorytm na jednym poziomie abstrakcji: sprawdź, uporządkuj, wypisz.
 * Method Object nie był potrzebny - etapy przekazują sobie po jednej wartości.
 */
public final class RepertoireBuilder {
    public String build(List<Screening> screenings) {
        List<Screening> validated = validateAndCopy(screenings);
        List<Screening> ordered = order(validated);
        return render(ordered);
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

    private String render(List<Screening> screenings) {
        StringBuilder text = new StringBuilder("REPERTUAR\n");
        for (Screening screening : screenings) {
            text.append(screening.start()).append(' ').append(screening.title())
                    .append(" (").append(screening.format()).append("), sala ")
                    .append(screening.hall()).append('\n');
        }
        if (screenings.isEmpty()) {
            text.append("brak seansow\n");
        }
        return text.toString();
    }
}
