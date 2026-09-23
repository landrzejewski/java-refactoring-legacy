package pl.training.workshop.m7.s11_middleman.step1;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Krok 1: plakietka sama decyduje, że nieznany seans wygląda jak wyprzedany
 * (zachowanie przeniesione z pośrednika).
 */
public final class SeatBadge {
    private final CinemaFacade cinema;

    public SeatBadge(CinemaFacade cinema) {
        this.cinema = Objects.requireNonNull(cinema, "cinema");
    }

    public String badge(String id) {
        int free = freeSeats(id);
        if (free == 0) {
            return id + ": WYPRZEDANE";
        }
        return cinema.title(id) + " (" + cinema.format(id) + "): " + free + " wolnych";
    }

    private int freeSeats(String id) {
        try {
            return cinema.freeSeats(id);
        } catch (NoSuchElementException e) {
            return 0;
        }
    }
}
