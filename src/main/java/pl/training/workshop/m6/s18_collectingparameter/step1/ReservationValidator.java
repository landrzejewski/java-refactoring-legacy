package pl.training.workshop.m6.s18_collectingparameter.step1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.training.workshop.m6.s18_collectingparameter.ReservationDraft;

/**
 * Krok 1: String zastąpiony listą - separator dokłada tylko String.join. Metody wciąż
 * tworzą własne listy, które validate skleja przez addAll.
 */
public final class ReservationValidator {
    public String validate(ReservationDraft draft) {
        List<String> warnings = new ArrayList<>();
        warnings.addAll(checkEmail(draft.email()));
        warnings.addAll(checkSeats(draft.seats()));
        if (!draft.now().isBefore(draft.showStart())) {
            warnings.add("seans juz sie rozpoczal");
        }
        return warnings.isEmpty() ? "OK" : String.join("; ", warnings);
    }

    private List<String> checkEmail(String email) {
        if (email == null || email.isBlank()) {
            return List.of("brak e-maila");
        }
        if (!email.contains("@")) {
            return List.of("niepoprawny e-mail: " + email);
        }
        return List.of();
    }

    private List<String> checkSeats(List<String> seats) {
        if (seats.isEmpty()) {
            return List.of("brak miejsc");
        }
        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        Set<String> reported = new HashSet<>();
        for (String seat : seats) {
            if (!seen.add(seat) && reported.add(seat)) {
                result.add("miejsce " + seat + " zdublowane");
            }
        }
        if (seats.size() >= 10) {
            result.add("grupa 10+: zastosuj rabat grupowy");
        }
        return result;
    }
}
