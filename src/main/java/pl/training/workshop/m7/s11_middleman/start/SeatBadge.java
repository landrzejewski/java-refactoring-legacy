package pl.training.workshop.m7.s11_middleman.start;

import java.util.Objects;

/** Klient 1: plakietka z liczbą wolnych miejsc. Korzysta z pośrednika. */
public final class SeatBadge {
    private final CinemaFacade cinema;

    public SeatBadge(CinemaFacade cinema) {
        this.cinema = Objects.requireNonNull(cinema, "cinema");
    }

    public String badge(String id) {
        int free = cinema.freeSeats(id);
        if (free == 0) {
            return id + ": WYPRZEDANE";
        }
        return cinema.title(id) + " (" + cinema.format(id) + "): " + free + " wolnych";
    }
}
