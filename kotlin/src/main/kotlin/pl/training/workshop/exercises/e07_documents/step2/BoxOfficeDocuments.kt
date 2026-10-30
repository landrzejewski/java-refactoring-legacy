package pl.training.workshop.exercises.e07_documents.step2

/** Krok 2: zestaw dokumentów kasy (A4). */
class BoxOfficeDocuments : DocumentFactory {
    override fun ticketLayout(): TicketLayout = BoxOfficeTicketLayout()

    override fun codeRenderer(): CodeRenderer = BoxOfficeCodeRenderer()

    override fun receiptLayout(): ReceiptLayout = BoxOfficeReceiptLayout()
}
