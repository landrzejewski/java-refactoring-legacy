package pl.training.workshop.m5.s05_extractsubclass.step4;

import pl.training.workshop.shared.Money;

/**
 * Krok 4 (rozwiązanie): flaga premiere usunięta - wariant wyraża klasa runtime.
 * Baza zna tylko zwykły seans; dopłatę premierową dodaje override price() w podklasie.
 */
public sealed class Screening permits PremiereScreening {
    private final String title;
    private final String format;

    protected Screening(String title, String format) {
        this.title = title;
        this.format = format;
    }

    public static Screening regular(String title, String format) {
        return new Screening(title, format);
    }

    public static Screening premiere(String title, String format, String guest) {
        return new PremiereScreening(title, format, guest);
    }

    public Money price() {
        return switch (format) {
            case "IMAX" -> Money.of("40.00");
            case "3D" -> Money.of("32.00");
            default -> Money.of("25.00");
        };
    }

    public String describe() {
        return title + " (" + format + ")";
    }
}
