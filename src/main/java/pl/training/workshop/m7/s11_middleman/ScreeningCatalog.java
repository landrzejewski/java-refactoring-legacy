package pl.training.workshop.m7.s11_middleman;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/** Stabilny kontrakt sceny: właściwy dostawca danych o seansach. Nieznany seans = wyjątek. */
public final class ScreeningCatalog {
    private final Map<String, Screening> screenings = new LinkedHashMap<>();

    public ScreeningCatalog(List<Screening> screenings) {
        screenings.forEach(screening -> this.screenings.put(screening.id(), screening));
    }

    public String title(String id) {
        return find(id).title();
    }

    public String format(String id) {
        return find(id).format();
    }

    public int freeSeats(String id) {
        return find(id).freeSeats();
    }

    public List<Screening> all() {
        return List.copyOf(screenings.values());
    }

    private Screening find(String id) {
        Screening screening = screenings.get(id);
        if (screening == null) {
            throw new NoSuchElementException("brak seansu " + id);
        }
        return screening;
    }
}
