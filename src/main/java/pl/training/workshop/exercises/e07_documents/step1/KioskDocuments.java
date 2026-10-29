package pl.training.workshop.exercises.e07_documents.step1;

/** Krok 1: zestaw dokumentów kiosku - jedyne miejsce, które zna klasy kioskowe. */
public final class KioskDocuments implements DocumentFactory {
    @Override
    public TicketLayout ticketLayout() {
        return new KioskTicketLayout();
    }

    @Override
    public CodeRenderer codeRenderer() {
        return new KioskCodeRenderer();
    }

    @Override
    public ReceiptLayout receiptLayout() {
        return new KioskReceiptLayout();
    }
}
