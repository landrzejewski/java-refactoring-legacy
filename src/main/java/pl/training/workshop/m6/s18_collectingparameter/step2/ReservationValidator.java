package pl.training.workshop.m6.s18_collectingparameter.step2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.training.workshop.m6.s18_collectingparameter.ReservationDraft;

/**
 * Krok 2: Move Accumulation to Collecting Parameter - metody dopisują do przekazanej listy
 * zamiast zwracać fragmenty. Nowa reguła to nowa metoda check...(draft, warnings).
 */
public final class ReservationValidator {
    public String validate(ReservationDraft draft) {
        List<String> warnings = new ArrayList<>();
        checkEmail(draft.email(), warnings);
        checkSeats(draft.seats(), warnings);
        checkShowTime(draft, warnings);
        return warnings.isEmpty() ? "OK" : String.join("; ", warnings);
    }

    private void checkEmail(String email, List<String> warnings) {
        if (email == null || email.isBlank()) {
            warnings.add("brak e-maila");
        } else if (!email.contains("@")) {
            warnings.add("niepoprawny e-mail: " + email);
        }
    }

    private void checkSeats(List<String> seats, List<String> warnings) {
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

    private void checkShowTime(ReservationDraft draft, List<String> warnings) {
        if (!draft.now().isBefore(draft.showStart())) {
            warnings.add("seans juz sie rozpoczal");
        }
    }
}
