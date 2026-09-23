package pl.training.workshop.m7.s07_arrowhead.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m7.s07_arrowhead.BookingAttempt;

/**
 * Krok 2: odwrócenie najbardziej zewnętrznego warunku - pierwsza guard clause
 * (brak seansu) i usunięcie jednego poziomu else. Test po każdym poziomie.
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
        String result;
        if (attempt.salesOpen()) {
            if (!attempt.customerBlocked()) {
                if (attempt.requestedSeats() > 0) {
                    if (attempt.requestedSeats() <= attempt.freeSeats()) {
                        result = "BOOKED";
                    } else {
                        result = "SOLD_OUT";
                    }
                } else {
                    result = "NO_SEATS_REQUESTED";
                }
            } else {
                result = "CUSTOMER_BLOCKED";
            }
        } else {
            result = "SALES_CLOSED";
        }
        return result;
    }

    public List<String> audit() {
        return List.copyOf(audit);
    }
}
