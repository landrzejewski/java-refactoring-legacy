package pl.training.workshop.exercises.e07_documents.step2;

/** Krok 2: zestaw dokumentów kasy (A4). */
public final class BoxOfficeDocuments implements DocumentFactory {
    @Override
    public TicketLayout ticketLayout() {
        return new BoxOfficeTicketLayout();
    }

    @Override
    public CodeRenderer codeRenderer() {
        return new BoxOfficeCodeRenderer();
    }

    @Override
    public ReceiptLayout receiptLayout() {
        return new BoxOfficeReceiptLayout();
    }
}
