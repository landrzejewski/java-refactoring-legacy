package pl.training.workshop.exercises.e07_documents.step1

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 1: bilet w aplikacji mobilnej - znaczniki HTML-podobne. */
internal class MobileTicketLayout : TicketLayout {
    override fun ticket(sale: Sale): String = "<ticket><h1>" + sale.movie + "</h1><p>Bilet " + sale.number +
        "</p><p>Miejsce " + sale.seat + "</p></ticket>"
}
