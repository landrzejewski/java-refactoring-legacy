package pl.training.workshop.exercises.e09_cashdrawer.step2

/**
 * Krok 2: wybór wariantu podziału po nazwie z konfiguracji - w korzeniu kompozycji, nie w regule.
 * Warianty są bezstanowe, więc współdzielimy jedne instancje.
 */
object DiscountSplits {
    private val PROPORTIONAL: DiscountSplit = ProportionalSplit()
    private val LAST: DiscountSplit = LastTicketSplit()
    private val EQUAL: DiscountSplit = EqualSplit()

    fun forMode(mode: String): DiscountSplit = when (mode) {
        "PROPORTIONAL" -> PROPORTIONAL
        "LAST" -> LAST
        "EQUAL" -> EQUAL
        else -> throw IllegalArgumentException("unknown split mode: $mode")
    }
}
