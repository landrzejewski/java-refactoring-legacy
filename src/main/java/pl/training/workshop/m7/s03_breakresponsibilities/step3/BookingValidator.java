package pl.training.workshop.m7.s03_breakresponsibilities.step3;

import java.util.Optional;

import pl.training.workshop.m7.s03_breakresponsibilities.BookingRequest;

/** Krok 1: Extract Class - reguły walidacji mają własnego właściciela (bez zmian w kroku 3). */
final class BookingValidator {
    Optional<String> firstError(BookingRequest request) {
        if (request.email() == null || !request.email().contains("@")) {
            return Optional.of("ERROR: niepoprawny e-mail");
        }
        if (request.seats().isEmpty()) {
            return Optional.of("ERROR: brak miejsc");
        }
        for (String seat : request.seats()) {
            if (!seat.matches("[A-L][0-9]{1,2}")) {
                return Optional.of("ERROR: niepoprawne miejsce " + seat);
            }
        }
        return Optional.empty();
    }
}
