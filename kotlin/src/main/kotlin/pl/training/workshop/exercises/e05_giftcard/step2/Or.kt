package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/**
 * Krok 2: alternatywa dwóch reguł. Tekstowa składnia z konfiguracji jeszcze jej nie zna,
 * ale w kodzie można ją już składać - nowy węzeł nie wymaga zmiany parsera ani Vouchers.
 */
data class Or(val left: VoucherRule, val right: VoucherRule) : VoucherRule {
    override fun applies(facts: ScreeningFacts): Boolean = left.applies(facts) || right.applies(facts)

    override fun describe(): String = left.describe() + " lub " + right.describe()
}
