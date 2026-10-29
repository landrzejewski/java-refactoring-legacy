package pl.training.workshop.exercises.e07_documents.step2;

import pl.training.workshop.exercises.e07_documents.Sale;

/** Krok 2: bilet z drukarki termicznej kiosku (32 kolumny). */
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
