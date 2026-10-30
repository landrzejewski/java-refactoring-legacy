package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/** Krok 2: koniunkcja dwóch reguł (wyrażenie nieterminalne). */
data class And(val left: VoucherRule, val right: VoucherRule) : VoucherRule {
    override fun applies(facts: ScreeningFacts): Boolean = left.applies(facts) && right.applies(facts)

    override fun describe(): String = left.describe() + " oraz " + right.describe()
}
