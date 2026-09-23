package pl.training.workshop.m6.s01_strategy.step3;

/**
 * Krok 3: wybór strategii przeniesiony do korzenia kompozycji (konfiguracja kina).
 * Strategie są bezstanowe, więc współdzielimy jedne instancje.
 */
public final class DiscountPrograms {
    private static final DiscountPolicy STANDARD = new StandardDiscount();
    private static final DiscountPolicy STUDENT_WEEK = new StudentWeekDiscount(STANDARD);
    private static final DiscountPolicy PREMIERE = new PremiereDiscount();

    private DiscountPrograms() {
    }

    public static DiscountPolicy forName(String program) {
        if (program == null) {
            throw new IllegalArgumentException("program must not be null");
        }
        return switch (program) {
            case "STANDARD" -> STANDARD;
            case "STUDENT_WEEK" -> STUDENT_WEEK;
            case "PREMIERE" -> PREMIERE;
            default -> throw new IllegalArgumentException("unknown program: " + program);
        };
    }
}
