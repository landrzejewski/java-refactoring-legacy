package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: bilet z drukarki termicznej kiosku (32 kolumny). */
class KioskTicketLayout : TicketLayout {
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
