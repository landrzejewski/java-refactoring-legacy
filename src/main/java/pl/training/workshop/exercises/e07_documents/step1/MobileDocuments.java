package pl.training.workshop.exercises.e07_documents.step1;

/** Krok 1: zestaw dokumentów aplikacji mobilnej. */
public final class MobileDocuments implements DocumentFactory {
    @Override
    public TicketLayout ticketLayout() {
        return new MobileTicketLayout();
    }

    @Override
    public CodeRenderer codeRenderer() {
        return new MobileCodeRenderer();
    }

    @Override
    public ReceiptLayout receiptLayout() {
        return new MobileReceiptLayout();
    }
}
