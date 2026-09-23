package pl.training.workshop.m5.s05_extractsubclass.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: Replace Constructor with Factory Method - regular(...) i premiere(...).
 * Punkty tworzenia są teraz w jednym miejscu; to one za chwilę wybiorą klasę runtime.
 */
public final class Screening {
    private final String title;
    private final String format;
    private final boolean premiere;
    private final String guest;

    private Screening(String title, String format, boolean premiere, String guest) {
        this.title = title;
        this.format = format;
        this.premiere = premiere;
        this.guest = guest;
    }

    public static Screening regular(String title, String format) {
        return new Screening(title, format, false, null);
    }

    public static Screening premiere(String title, String format, String guest) {
        return new Screening(title, format, true, guest);
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
        String text = title + " (" + format + ")";
        if (premiere) {
            text += " - premiera, gość: " + guest;
        }
        return text;
    }
}
