package pl.training.workshop.m8.s04_shadowlimits.start;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;
import pl.training.workshop.m8.s04_shadowlimits.Infrastructure;

/**
 * Start: tryb shadow "jak dla kalkulatora" zastosowany do ścieżki z efektami ubocznymi.
 * Wynik jest z legacy, ale kandydat też obciąża kartę, zapisuje i wysyła mail -
 * klient dostaje dwa maile i dwa obciążenia. Porównujemy tylko zwracany tekst.
 */
public final class ShadowBooking {
    private final LegacyBookingFlow legacy;
    private final NewBookingFlow candidate;
    private final List<String> divergences = new ArrayList<>();

    public ShadowBooking(Infrastructure infra) {
        this.legacy = new LegacyBookingFlow(infra);
        this.candidate = new NewBookingFlow(infra);
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
