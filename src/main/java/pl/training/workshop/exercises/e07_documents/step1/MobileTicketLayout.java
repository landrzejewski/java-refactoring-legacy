package pl.training.workshop.exercises.e07_documents.step1;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 1: bilet w aplikacji mobilnej - znaczniki HTML-podobne. */
final class MobileTicketLayout implements TicketLayout {
    @Override
    public String ticket(Sale sale) {
        return "<ticket><h1>" + sale.movie() + "</h1><p>Bilet " + sale.number()
                + "</p><p>Miejsce " + sale.seat() + "</p></ticket>";
    }
}
