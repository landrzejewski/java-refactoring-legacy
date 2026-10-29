package pl.training.workshop.exercises.e07_documents.step2;

/**
 * Krok 2: rodzina dokumentów jednego kanału. Klient dostaje cały spójny zestaw
 * (bilet, kod, paragon) z jednego miejsca i nie może pomieszać elementów różnych kanałów.
 */
public interface DocumentFactory {
    TicketLayout ticketLayout();

    CodeRenderer codeRenderer();

    ReceiptLayout receiptLayout();
}
