package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: układ biletu w danym kanale. */
fun interface TicketLayout {
    fun ticket(sale: Sale): String
}
