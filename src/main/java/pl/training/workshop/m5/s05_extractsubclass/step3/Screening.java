package pl.training.workshop.m5.s05_extractsubclass.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: pole guest zniknęło z bazy (Push Down Field) - razem z gałęzią premierową describe(). */
public sealed class Screening permits PremiereScreening {
    private final String title;
    private final String format;
    private final boolean premiere;

    protected Screening(String title, String format, boolean premiere) {
        this.title = title;
        this.format = format;
        this.premiere = premiere;
    }

    public static Screening regular(String title, String format) {
        return new Screening(title, format, false);
    }

    public static Screening premiere(String title, String format, String guest) {
        return new PremiereScreening(title, format, guest);
    }

    public Money price() {
        Money base = switch (format) {
            case "IMAX" -> Money.of("40.00");
            case "3D" -> Money.of("32.00");
            default -> Money.of("25.00");
        };
        return premiere ? base.plus(Money.of("15.00")) : base;
    }

    public String describe() {
        return title + " (" + format + ")";
    }
}
