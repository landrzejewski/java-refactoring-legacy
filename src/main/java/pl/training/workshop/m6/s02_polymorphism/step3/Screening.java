package pl.training.workshop.m6.s02_polymorphism.step3;

import pl.training.workshop.m6.s02_polymorphism.ScreeningRow;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: forma Java 25 - sealed interface i rekordy. Zestaw rodzajów jest zamknięty,
 * więc kompilator sprawdza wyczerpujące switche także u klientów.
 */
public sealed interface Screening permits RegularScreening, PremiereScreening, MarathonScreening {
    static Screening fromRow(ScreeningRow row) {
        return switch (row.kind()) {
            case "REGULAR" -> new RegularScreening(row.title(), row.value());
            case "PREMIERE" -> new PremiereScreening(row.title(), row.value());
            case "MARATHON" -> new MarathonScreening(row.title(), row.value());
            default -> throw new IllegalArgumentException("unknown screening kind: " + row.kind());
        };
    }

    String title();

    String label();

    int durationMinutes();

    Money price();
}
