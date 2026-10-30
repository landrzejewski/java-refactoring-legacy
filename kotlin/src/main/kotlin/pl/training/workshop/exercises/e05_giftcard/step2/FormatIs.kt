package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.exercises.e05_giftcard.ScreeningFacts

/** Krok 2: wyrażenie terminalne - seans ma dany format (2D, 3D, IMAX). */
data class FormatIs(val format: String) : VoucherRule {
    override fun applies(facts: ScreeningFacts): Boolean = facts.format == format

    override fun describe(): String = "format $format"
}
