package pl.training.workshop.exercises.e09_cashdrawer.step2;

/**
 * Krok 2: wybór wariantu podziału po nazwie z konfiguracji - w korzeniu kompozycji, nie w regule.
 * Warianty są bezstanowe, więc współdzielimy jedne instancje.
 */
public final class DiscountSplits {
    private static final DiscountSplit PROPORTIONAL = new ProportionalSplit();
    private static final DiscountSplit LAST = new LastTicketSplit();
    private static final DiscountSplit EQUAL = new EqualSplit();

    private DiscountSplits() {
    }

    public static DiscountSplit forMode(String mode) {
        return switch (mode) {
            case "PROPORTIONAL" -> PROPORTIONAL;
            case "LAST" -> LAST;
            case "EQUAL" -> EQUAL;
            default -> throw new IllegalArgumentException("unknown split mode: " + mode);
        };
    }
}
