package pl.training.workshop.exercises.e07_documents.step1

/** Krok 1: zestaw dokumentów aplikacji mobilnej. */
class MobileDocuments : DocumentFactory {
    override fun ticketLayout(): TicketLayout = MobileTicketLayout()

    override fun codeRenderer(): CodeRenderer = MobileCodeRenderer()

    override fun receiptLayout(): ReceiptLayout = MobileReceiptLayout()
}
