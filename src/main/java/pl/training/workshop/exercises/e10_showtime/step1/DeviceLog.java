package pl.training.workshop.exercises.e10_showtime.step1;

import java.util.ArrayList;
import java.util.List;

/** Krok 1: wspólny dziennik zdarzeń urządzeń i podsystemów - kolejność wpisów jest obserwowalnym zachowaniem sali. */
public final class DeviceLog {
    private final List<String> lines = new ArrayList<>();

    public void add(String line) {
        lines.add(line);
    }

    public String dump() {
        return String.join("\n", lines);
    }
}
