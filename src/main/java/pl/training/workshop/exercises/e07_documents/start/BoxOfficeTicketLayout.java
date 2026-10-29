package pl.training.workshop.exercises.e07_documents.start;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Start: bilet A4 drukowany w kasie. */
public final class BoxOfficeTicketLayout implements TicketLayout {
    @Override
    public String ticket(Sale sale) {
        return String.join("\n",
                "CINELEGACY - BILET WSTEPU",
                "Numer:   " + sale.number(),
                "Film:    " + sale.movie(),
                "Miejsce: " + sale.seat());
    }
}
