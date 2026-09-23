package pl.training.workshop.m6.s02_polymorphism.step1;

import pl.training.workshop.m6.s02_polymorphism.ScreeningRow;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Extract Subclass dla jednej gałęzi (MARATHON). Tworzenie w fromRow kieruje maraton
 * do podklasy, a gałęzie MARATHON w switchach stały się martwe - rzucają wyjątek.
 */
public sealed class Screening permits MarathonScreening {
    public enum Kind { REGULAR, PREMIERE, MARATHON }

    private final Kind kind;
    private final String title;
    private final int value;

    Screening(Kind kind, String title, int value) {
        this.kind = kind;
        this.title = title;
        this.value = value;
    }

    public static Screening fromRow(ScreeningRow row) {
        return switch (row.kind()) {
            case "REGULAR" -> new Screening(Kind.REGULAR, row.title(), row.value());
            case "PREMIERE" -> new Screening(Kind.PREMIERE, row.title(), row.value());
            case "MARATHON" -> new MarathonScreening(row.title(), row.value());
            default -> throw new IllegalArgumentException("unknown screening kind: " + row.kind());
        };
    }

    protected final String title() {
        return title;
    }

    public String label() {
        return switch (kind) {
            case REGULAR -> title;
            case PREMIERE -> "Premiera: " + title;
            case MARATHON -> throw new IllegalStateException("handled by MarathonScreening");
        };
    }

    public int durationMinutes() {
        return switch (kind) {
            case REGULAR -> 20 + value;
            case PREMIERE -> 30 + value;
            case MARATHON -> throw new IllegalStateException("handled by MarathonScreening");
        };
    }

    public Money price() {
        return switch (kind) {
            case REGULAR -> Money.of("25.00");
            case PREMIERE -> Money.of("35.00");
            case MARATHON -> throw new IllegalStateException("handled by MarathonScreening");
        };
    }
}
