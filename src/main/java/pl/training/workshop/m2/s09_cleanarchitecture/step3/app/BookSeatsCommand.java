package pl.training.workshop.m2.s09_cleanarchitecture.step3.app;

import java.util.List;

/** Krok 1: dane wejściowe przypadku użycia - prosty rekord, bez Map z HTTP. */
public record BookSeatsCommand(String email, String format, List<Integer> rows) {
    public BookSeatsCommand {
        rows = List.copyOf(rows);
    }
}
