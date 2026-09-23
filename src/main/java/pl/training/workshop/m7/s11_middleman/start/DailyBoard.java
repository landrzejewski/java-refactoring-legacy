package pl.training.workshop.m7.s11_middleman.start;

import java.util.Objects;
import java.util.stream.Collectors;

/** Klient 2: tablica seansów w holu. Korzysta z pośrednika. */
public final class DailyBoard {
    private final CinemaFacade cinema;

    public DailyBoard(CinemaFacade cinema) {
        this.cinema = Objects.requireNonNull(cinema, "cinema");
    }

    public String render() {
        return cinema.screenings().stream()
                .map(screening -> screening.id() + " " + screening.title() + " " + screening.format())
                .collect(Collectors.joining("\n"));
    }
}
