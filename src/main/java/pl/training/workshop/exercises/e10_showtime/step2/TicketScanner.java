package pl.training.workshop.exercises.e10_showtime.step2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Krok 2: bramka biletowa - liczy skany i zamyka wejście na seans. */
final class TicketScanner {
    private final DeviceLog log;
    private final Map<String, Integer> scanned = new HashMap<>();
    private final Set<String> closed = new HashSet<>();

    public TicketScanner(DeviceLog log) {
        this.log = log;
    }

    public void scan(String screeningId) {
        if (closed.contains(screeningId)) {
            throw new IllegalStateException("scanner " + screeningId + " already closed");
        }
        scanned.merge(screeningId, 1, Integer::sum);
    }

    /** Zamyka wejście i zwraca liczbę wpuszczonych widzów. */
    public int close(String screeningId) {
        if (!closed.add(screeningId)) {
            throw new IllegalStateException("scanner " + screeningId + " already closed");
        }
        int count = scanned.getOrDefault(screeningId, 0);
        log.add("scanner: " + screeningId + " closed, " + count + " scanned");
        return count;
    }
}
