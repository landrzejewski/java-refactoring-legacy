package pl.training.workshop.m6.s02_polymorphism.step2;

import pl.training.workshop.m6.s02_polymorphism.ScreeningRow;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: pozostałe gałęzie w podklasach, Screening jest abstrakcyjny, pole kind i enum Kind
 * usunięte. Jedyny switch został w miejscu tworzenia (mapowanie wiersza z bazy).
 */
public abstract sealed class Screening permits RegularScreening, PremiereScreening, MarathonScreening {
    private final String title;

    Screening(String title) {
        this.title = title;
    }

    public static Screening fromRow(ScreeningRow row) {
        return switch (row.kind()) {
            case "REGULAR" -> new RegularScreening(row.title(), row.value());
            case "PREMIERE" -> new PremiereScreening(row.title(), row.value());
            case "MARATHON" -> new MarathonScreening(row.title(), row.value());
            default -> throw new IllegalArgumentException("unknown screening kind: " + row.kind());
        };
    }

    protected final String title() {
        return title;
    }

    public abstract String label();

    public abstract int durationMinutes();

    public abstract Money price();
}
