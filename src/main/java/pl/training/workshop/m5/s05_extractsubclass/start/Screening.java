package pl.training.workshop.m5.s05_extractsubclass.start;

import pl.training.workshop.shared.Money;

/**
 * Start: flaga {@code premiere} i pole {@code guest}, które ma sens tylko dla premier.
 * Dla zwykłego seansu guest == null, a każda metoda powtarza "if (premiere)".
 */
public final class Screening {
    private final String title;
    private final String format;
    private final boolean premiere;
    private final String guest;

    public Screening(String title, String format, boolean premiere, String guest) {
        this.title = title;
        this.format = format;
        this.premiere = premiere;
        this.guest = guest;
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
