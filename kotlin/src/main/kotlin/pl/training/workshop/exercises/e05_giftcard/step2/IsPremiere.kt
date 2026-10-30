package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/** Krok 2: wyrażenie terminalne - seans jest premierą (bez pól, więc w Kotlinie `data object`). */
data object IsPremiere : VoucherRule {
    override fun applies(facts: ScreeningFacts): Boolean = facts.premiere

    override fun describe(): String = "premiera"
}
