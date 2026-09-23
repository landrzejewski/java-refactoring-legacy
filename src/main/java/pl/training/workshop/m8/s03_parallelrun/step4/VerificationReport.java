package pl.training.workshop.m8.s03_parallelrun.step4;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m8.s03_parallelrun.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Krok 4 (bez zmian): raport rozbieżności - zgodność, rozbieżność albo awaria kandydata.
 * Każde porównanie to typowane zdarzenie, a nie wpis w logu do parsowania.
 */
public final class VerificationReport {
    public sealed interface Verification {
    }

    public record Agreement(TicketQuery query, Money price) implements Verification {
    }

    public record Divergence(TicketQuery query, Money legacy, Money candidate) implements Verification {
    }

    public record CandidateFailure(TicketQuery query, Money legacy, String error) implements Verification {
    }

    private final List<Verification> entries = new ArrayList<>();

    public void record(Verification verification) {
        entries.add(verification);
    }

    public List<Verification> entries() {
        return List.copyOf(entries);
    }

    /** Czytelne wiersze raportu - tylko to, co wymaga uwagi zespołu. */
    public List<String> problems() {
        List<String> lines = new ArrayList<>();
        for (Verification entry : entries) {
            switch (entry) {
                case Agreement agreement -> { }
                case Divergence d -> lines.add("ROZBIEZNOSC " + d.query()
                        + ": legacy " + d.legacy() + ", kandydat " + d.candidate());
                case CandidateFailure f -> lines.add("BLAD KANDYDATA " + f.query() + ": " + f.error());
            }
        }
        return lines;
    }
}
