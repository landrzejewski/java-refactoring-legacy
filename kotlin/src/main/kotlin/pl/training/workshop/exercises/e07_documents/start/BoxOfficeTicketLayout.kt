package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/** Start: bilet A4 drukowany w kasie. */
class BoxOfficeTicketLayout : TicketLayout {
    override fun ticket(sale: Sale): String = listOf(
        "CINELEGACY - BILET WSTEPU",
        "Numer:   " + sale.number,
        "Film:    " + sale.movie,
        "Miejsce: " + sale.seat
    ).joinToString("\n")
}
