package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts;

/** Krok 2: koniunkcja dwóch reguł (wyrażenie nieterminalne). */
public record And(VoucherRule left, VoucherRule right) implements VoucherRule {
    @Override
    public boolean applies(ScreeningFacts facts) {
        return left.applies(facts) && right.applies(facts);
    }

    @Override
    public String describe() {
        return left.describe() + " oraz " + right.describe();
    }
}
