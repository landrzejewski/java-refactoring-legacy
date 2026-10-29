package pl.training.workshop.exercises.e07_documents.step1;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 1: bilet z drukarki termicznej kiosku (32 kolumny). */
final class KioskTicketLayout implements TicketLayout {
    static final String RULE = "*".repeat(32);

    @Override
    public String ticket(Sale sale) {
        return String.join("\n",
                RULE,
                "BILET " + sale.number(),
                sale.movie(),
                "Miejsce " + sale.seat());
    }
}
