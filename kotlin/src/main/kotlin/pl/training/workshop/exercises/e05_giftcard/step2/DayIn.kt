package pl.training.workshop.exercises.e05_giftcard.step2

import java.time.DayOfWeek

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/** Krok 2: wyrażenie terminalne - dzień seansu należy do listy (w kolejności z reguły). */
data class DayIn(val days: List<DayOfWeek>) : VoucherRule {
    override fun applies(facts: ScreeningFacts): Boolean = facts.day in days

    override fun describe(): String = "dzień: " + days.joinToString(", ") { day -> day.name.substring(0, 3) }
}
