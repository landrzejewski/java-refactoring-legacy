package pl.training.workshop.exercises.e07_documents.start;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Start: bilet w aplikacji mobilnej - znaczniki HTML-podobne. */
public final class MobileTicketLayout implements TicketLayout {
    @Override
    public String ticket(Sale sale) {
        return "<ticket><h1>" + sale.movie() + "</h1><p>Bilet " + sale.number()
                + "</p><p>Miejsce " + sale.seat() + "</p></ticket>";
    }
}
