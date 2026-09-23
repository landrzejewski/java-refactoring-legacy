package pl.training.workshop.m7.s11_middleman.step2;

import java.util.Objects;
import java.util.stream.Collectors;

/** Klient 2: bez zmian w kroku 2 - nadal korzysta z pośrednika (migrujemy po jednym kliencie). */
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
