package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts;

/** Krok 2: wyrażenie terminalne - seans jest premierą. */
public record IsPremiere() implements VoucherRule {
    @Override
    public boolean applies(ScreeningFacts facts) {
        return facts.premiere();
    }

    @Override
    public String describe() {
        return "premiera";
    }
}
