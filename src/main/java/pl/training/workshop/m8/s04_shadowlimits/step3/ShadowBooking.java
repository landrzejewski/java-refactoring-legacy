package pl.training.workshop.m8.s04_shadowlimits.step3;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;
import pl.training.workshop.m8.s04_shadowlimits.Infrastructure;

/**
 * Krok 3: cień woła tylko czysty BookingPlanner - bez nagrywarek i bez ryzyka, że ktoś
 * podepnie prawdziwy adapter. Podwójne wykonanie dotyczy wyłącznie obliczenia.
 */
public final class ShadowBooking {
    private final Infrastructure infra;
    private final LegacyBookingFlow legacy;
    private final BookingPlanner candidate = new BookingPlanner();
    private final List<String> divergences = new ArrayList<>();

    public ShadowBooking(Infrastructure infra) {
        this.infra = infra;
        this.legacy = new LegacyBookingFlow(infra);
    }

    public String book(BookingRequest request) {
        int before = infra.log().size();
        String result = legacy.book(request);
        List<String> legacyEffects = infra.log().subList(before, infra.log().size());
        try {
            BookingPlan plan = candidate.plan(request);
            compare("wynik", List.of(result), List.of(plan.result()));
            List<String> planned = plan.effects().stream().map(BookingPlan.Effect::describe).toList();
            compare("efekty", legacyEffects, planned);
        } catch (RuntimeException failure) {
            divergences.add("kandydat: " + failure.getMessage());
        }
        return result;
    }

    private void compare(String what, List<String> legacyValues, List<String> candidateValues) {
        for (int i = 0; i < Math.max(legacyValues.size(), candidateValues.size()); i++) {
            String legacyValue = i < legacyValues.size() ? legacyValues.get(i) : "-";
            String candidateValue = i < candidateValues.size() ? candidateValues.get(i) : "-";
            if (!legacyValue.equals(candidateValue)) {
                divergences.add(what + " legacy: " + legacyValue + " | kandydat: " + candidateValue);
            }
        }
    }

    public List<String> divergences() {
        return List.copyOf(divergences);
    }
}
