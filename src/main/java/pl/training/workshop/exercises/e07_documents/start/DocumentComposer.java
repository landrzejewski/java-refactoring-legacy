package pl.training.workshop.exercises.e07_documents.start;

import pl.training.workshop.exercises.e07_documents.Sale;

/**
 * Start: skleja trzy części dokumentu. Przyjmuje je osobno, więc nic nie broni
 * złożyć biletu kioskowego z kodem QR aplikacji i paragonem A4 - kompilator tego nie zauważy.
 */
public final class DocumentComposer {
    private final TicketLayout ticketLayout;
    private final CodeRenderer codeRenderer;
    private final ReceiptLayout receiptLayout;

    public DocumentComposer(TicketLayout ticketLayout, CodeRenderer codeRenderer, ReceiptLayout receiptLayout) {
        this.ticketLayout = ticketLayout;
        this.codeRenderer = codeRenderer;
        this.receiptLayout = receiptLayout;
    }

    public String compose(Sale sale) {
        return ticketLayout.ticket(sale) + "\n" + codeRenderer.code(sale) + "\n" + receiptLayout.receipt(sale);
    }
}
