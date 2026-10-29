package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts;

/** Krok 2: negacja reguły. */
public record Not(VoucherRule rule) implements VoucherRule {
    @Override
    public boolean applies(ScreeningFacts facts) {
        return !rule.applies(facts);
    }

    @Override
    public String describe() {
        return "nie " + rule.describe();
    }
}
