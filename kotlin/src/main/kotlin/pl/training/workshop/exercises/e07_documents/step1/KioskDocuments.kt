package pl.training.workshop.exercises.e07_documents.step1

/** Krok 1: zestaw dokumentów kiosku - jedyne miejsce, które zna klasy kioskowe. */
class KioskDocuments : DocumentFactory {
    override fun ticketLayout(): TicketLayout = KioskTicketLayout()

    override fun codeRenderer(): CodeRenderer = KioskCodeRenderer()

    override fun receiptLayout(): ReceiptLayout = KioskReceiptLayout()
}
