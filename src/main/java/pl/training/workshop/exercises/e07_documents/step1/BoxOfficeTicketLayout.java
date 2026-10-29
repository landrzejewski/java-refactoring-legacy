package pl.training.workshop.exercises.e07_documents.step1;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 1: bilet A4 drukowany w kasie. */
final class BoxOfficeTicketLayout implements TicketLayout {
    @Override
    public String ticket(Sale sale) {
        return String.join("\n",
                "CINELEGACY - BILET WSTEPU",
                "Numer:   " + sale.number(),
                "Film:    " + sale.movie(),
                "Miejsce: " + sale.seat());
    }
}
