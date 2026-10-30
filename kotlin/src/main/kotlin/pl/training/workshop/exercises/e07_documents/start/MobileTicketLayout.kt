package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: bilet w aplikacji mobilnej - znaczniki HTML-podobne. */
class MobileTicketLayout : TicketLayout {
    override fun ticket(sale: Sale): String = "<ticket><h1>" + sale.movie + "</h1><p>Bilet " + sale.number +
        "</p><p>Miejsce " + sale.seat + "</p></ticket>"
}
