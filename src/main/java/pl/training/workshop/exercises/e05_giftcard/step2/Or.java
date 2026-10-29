package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts;

/**
 * Krok 2: alternatywa dwóch reguł. Tekstowa składnia z konfiguracji jeszcze jej nie zna,
 * ale w kodzie można ją już składać - nowy węzeł nie wymaga zmiany parsera ani Vouchers.
 */
public record Or(VoucherRule left, VoucherRule right) implements VoucherRule {
    @Override
    public boolean applies(ScreeningFacts facts) {
        return left.applies(facts) || right.applies(facts);
    }

    @Override
    public String describe() {
        return left.describe() + " lub " + right.describe();
    }
}
