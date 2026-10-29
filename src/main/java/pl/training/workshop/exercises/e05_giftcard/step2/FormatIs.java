package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts;

/** Krok 2: wyrażenie terminalne - seans ma dany format (2D, 3D, IMAX). */
public record FormatIs(String format) implements VoucherRule {
    @Override
    public boolean applies(ScreeningFacts facts) {
        return facts.format().equals(format);
    }

    @Override
    public String describe() {
        return "format " + format;
    }
}
