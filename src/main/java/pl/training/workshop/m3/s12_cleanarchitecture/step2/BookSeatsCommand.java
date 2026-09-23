package pl.training.workshop.m3.s12_cleanarchitecture.step2;

import java.util.List;

/** Krok 1: dane wejściowe przypadku użycia - prosty rekord, bez Map z HTTP. */
public record BookSeatsCommand(String email, String format, List<Integer> rows) {
    public BookSeatsCommand {
        rows = List.copyOf(rows);
    }
}
