package pl.training.workshop.m7.s07_arrowhead.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m7.s07_arrowhead.BookingAttempt;

/**
 * Krok 1: Extract Method - decyzja (cały grot) wydzielona do decide(), a efekt uboczny
 * (audyt) zostaje w book(). Teraz wczesne return w decide() nie ominą audytu.
 */
public final class BookingGate {
    private final List<String> audit = new ArrayList<>();

    public String book(BookingAttempt attempt) {
        String result = decide(attempt);
        audit.add(attempt.email() + " -> " + result);
        return result;
    }

    private String decide(BookingAttempt attempt) {
        String result;
        if (attempt.screeningFound()) {
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
        } else {
            result = "NO_SCREENING";
        }
        return result;
    }

    public List<String> audit() {
        return List.copyOf(audit);
    }
}
