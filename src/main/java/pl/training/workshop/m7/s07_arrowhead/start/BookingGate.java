package pl.training.workshop.m7.s07_arrowhead.start;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m7.s07_arrowhead.BookingAttempt;

/**
 * Start: grot strzały - główna ścieżka (BOOKED) schowana na piątym poziomie zagnieżdżenia,
 * wynik zbierany w zmiennej result. Uwaga: wpis do audytu na końcu dotyczy KAŻDEJ ścieżki.
 */
public final class BookingGate {
    private final List<String> audit = new ArrayList<>();

    public String book(BookingAttempt attempt) {
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
        audit.add(attempt.email() + " -> " + result);
        return result;
    }

    public List<String> audit() {
        return List.copyOf(audit);
    }
}
