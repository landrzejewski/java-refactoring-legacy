package pl.training.workshop.m6.s18_collectingparameter.step3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.training.workshop.m6.s18_collectingparameter.ReservationDraft;

/**
 * Krok 3: parametr zbierający ma własny, wąski typ Warnings (tylko add). Właścicielem
 * kolekcji jest validate - tworzy ją i decyduje o formacie wyniku.
 */
public final class ReservationValidator {
    public String validate(ReservationDraft draft) {
        Warnings warnings = new Warnings();
        checkEmail(draft.email(), warnings);
        checkSeats(draft.seats(), warnings);
        checkShowTime(draft, warnings);
        return warnings.summary();
    }

    private void checkEmail(String email, Warnings warnings) {
        if (email == null || email.isBlank()) {
            warnings.add("brak e-maila");
        } else if (!email.contains("@")) {
            warnings.add("niepoprawny e-mail: " + email);
        }
    }

    private void checkSeats(List<String> seats, Warnings warnings) {
        if (seats.isEmpty()) {
            warnings.add("brak miejsc");
            return;
        }
        Set<String> seen = new HashSet<>();
        Set<String> reported = new HashSet<>();
        for (String seat : seats) {
            if (!seen.add(seat) && reported.add(seat)) {
                warnings.add("miejsce " + seat + " zdublowane");
            }
        }
        if (seats.size() >= 10) {
            warnings.add("grupa 10+: zastosuj rabat grupowy");
        }
    }

    private void checkShowTime(ReservationDraft draft, Warnings warnings) {
        if (!draft.now().isBefore(draft.showStart())) {
            warnings.add("seans juz sie rozpoczal");
        }
    }
}
