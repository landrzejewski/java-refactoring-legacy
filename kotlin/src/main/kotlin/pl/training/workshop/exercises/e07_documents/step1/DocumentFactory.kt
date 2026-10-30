package pl.training.workshop.exercises.e07_documents.step1

/**
 * Krok 1: rodzina dokumentów jednego kanału. Klient dostaje cały spójny zestaw
 * (bilet, kod, paragon) z jednego miejsca i nie może pomieszać elementów różnych kanałów.
 */
interface DocumentFactory {
    fun ticketLayout(): TicketLayout

    fun codeRenderer(): CodeRenderer

    fun receiptLayout(): ReceiptLayout
}
