package pl.training.workshop.m7.s07_arrowhead.step3;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m7.s07_arrowhead.BookingAttempt;

/**
 * Krok 3 (rozwiązanie): pozostałe poziomy spłaszczone do guard clauses, zmienna result
 * zniknęła. Kolejność warunków identyczna jak w start - priorytet błędów zachowany.
 */
public final class BookingGate {
    private final List<String> audit = new ArrayList<>();

    public String book(BookingAttempt attempt) {
        String result = decide(attempt);
        audit.add(attempt.email() + " -> " + result);
        return result;
    }

    private String decide(BookingAttempt attempt) {
        if (!attempt.screeningFound()) {
            return "NO_SCREENING";
        }
        if (!attempt.salesOpen()) {
            return "SALES_CLOSED";
        }
        if (attempt.customerBlocked()) {
            return "CUSTOMER_BLOCKED";
        }
        if (attempt.requestedSeats() <= 0) {
            return "NO_SEATS_REQUESTED";
        }
        if (attempt.requestedSeats() > attempt.freeSeats()) {
            return "SOLD_OUT";
        }
        return "BOOKED";
    }

    public List<String> audit() {
        return List.copyOf(audit);
    }
}
