package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 2: bilet z drukarki termicznej kiosku (32 kolumny). */
internal class KioskTicketLayout : TicketLayout {
    override fun ticket(sale: Sale): String = listOf(
        RULE,
        "BILET " + sale.number,
        sale.movie,
        "Miejsce " + sale.seat
    ).joinToString("\n")

    companion object {
        internal val RULE = "*".repeat(32)
    }
}
