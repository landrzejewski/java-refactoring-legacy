package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/** Krok 2: negacja reguły. */
data class Not(val rule: VoucherRule) : VoucherRule {
    override fun applies(facts: ScreeningFacts): Boolean = !rule.applies(facts)

    override fun describe(): String = "nie " + rule.describe()
}
