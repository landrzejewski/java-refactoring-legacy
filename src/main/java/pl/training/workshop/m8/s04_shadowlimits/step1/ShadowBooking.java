package pl.training.workshop.m8.s04_shadowlimits.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;
import pl.training.workshop.m8.s04_shadowlimits.Infrastructure;

/**
 * Krok 1: cień nadal podaje kandydatowi prawdziwe efekty - podwójne maile i obciążenia zostają.
 * Ale mamy już szew: w następnym kroku wystarczy podać inną implementację Effects.
 */
public final class ShadowBooking {
    private final LegacyBookingFlow legacy;
    private final NewBookingFlow candidate;
    private final List<String> divergences = new ArrayList<>();

    public ShadowBooking(Infrastructure infra) {
        this.legacy = new LegacyBookingFlow(infra);
        this.candidate = new NewBookingFlow(new RealEffects(infra));
    }

    public String book(BookingRequest request) {
        String result = legacy.book(request);
        try {
            String shadow = candidate.book(request);
            if (!shadow.equals(result)) {
                divergences.add("legacy: " + result + " | kandydat: " + shadow);
            }
        } catch (RuntimeException failure) {
            divergences.add("kandydat: " + failure.getMessage());
        }
        return result;
    }

    public List<String> divergences() {
        return List.copyOf(divergences);
    }
}
