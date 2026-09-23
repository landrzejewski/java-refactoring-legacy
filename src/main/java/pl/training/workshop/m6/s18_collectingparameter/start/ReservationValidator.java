package pl.training.workshop.m6.s18_collectingparameter.start;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.training.workshop.m6.s18_collectingparameter.ReservationDraft;

/**
 * Start: ostrzeżenia sklejane w String - każda metoda pomocnicza zwraca fragment z "; ",
 * a na końcu obcinamy dwa ostatnie znaki. Łatwo o zgubiony separator.
 */
public final class ReservationValidator {
    public String validate(ReservationDraft draft) {
        String warnings = "";
        warnings += checkEmail(draft.email());
        warnings += checkSeats(draft.seats());
        if (!draft.now().isBefore(draft.showStart())) {
            warnings += "seans juz sie rozpoczal; ";
        }
        return warnings.isEmpty() ? "OK" : warnings.substring(0, warnings.length() - 2);
    }

    private String checkEmail(String email) {
        if (email == null || email.isBlank()) {
            return "brak e-maila; ";
        }
        if (!email.contains("@")) {
            return "niepoprawny e-mail: " + email + "; ";
        }
        return "";
    }

    private String checkSeats(List<String> seats) {
        if (seats.isEmpty()) {
            return "brak miejsc; ";
        }
        String result = "";
        Set<String> seen = new HashSet<>();
        Set<String> reported = new HashSet<>();
        for (String seat : seats) {
            if (!seen.add(seat) && reported.add(seat)) {
                result += "miejsce " + seat + " zdublowane; ";
            }
        }
        if (seats.size() >= 10) {
            result += "grupa 10+: zastosuj rabat grupowy; ";
        }
        return result;
    }
}
