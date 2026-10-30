package pl.training.workshop.exercises.e07_documents.step1

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 1: bilet A4 drukowany w kasie. */
internal class BoxOfficeTicketLayout : TicketLayout {
    override fun ticket(sale: Sale): String = listOf(
        "CINELEGACY - BILET WSTEPU",
        "Numer:   " + sale.number,
        "Film:    " + sale.movie,
        "Miejsce: " + sale.seat
    ).joinToString("\n")
}
