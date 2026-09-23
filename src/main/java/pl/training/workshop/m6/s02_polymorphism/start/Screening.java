package pl.training.workshop.m6.s02_polymorphism.start;

import pl.training.workshop.m6.s02_polymorphism.ScreeningRow;
import pl.training.workshop.shared.Money;

/**
 * Start: ten sam switch po rodzaju seansu w trzech metodach, a pole value znaczy raz
 * "minuty", raz "liczba filmów". Nowy rodzaj seansu = zmiana we wszystkich switchach.
 */
public final class Screening {
    public enum Kind { REGULAR, PREMIERE, MARATHON }

    private final Kind kind;
    private final String title;
    private final int value;

    private Screening(Kind kind, String title, int value) {
        this.kind = kind;
        this.title = title;
        this.value = value;
    }

    public static Screening fromRow(ScreeningRow row) {
        Kind kind = switch (row.kind()) {
            case "REGULAR" -> Kind.REGULAR;
            case "PREMIERE" -> Kind.PREMIERE;
            case "MARATHON" -> Kind.MARATHON;
            default -> throw new IllegalArgumentException("unknown screening kind: " + row.kind());
        };
        return new Screening(kind, row.title(), row.value());
    }

    public String label() {
        return switch (kind) {
            case REGULAR -> title;
            case PREMIERE -> "Premiera: " + title;
            case MARATHON -> "Maraton: " + title + " (" + value + " filmy)";
        };
    }

    public int durationMinutes() {
        return switch (kind) {
            case REGULAR -> 20 + value;
            case PREMIERE -> 30 + value;
            case MARATHON -> value * 120 + (value - 1) * 15;
        };
    }

    public Money price() {
        return switch (kind) {
            case REGULAR -> Money.of("25.00");
            case PREMIERE -> Money.of("35.00");
            case MARATHON -> Money.of("20.00").times(value);
        };
    }
}
