package pl.training.workshop.exercises.e07_documents.step2

/** Krok 2: zestaw dokumentów kiosku - jedyne miejsce, które zna klasy kioskowe. */
class KioskDocuments : DocumentFactory {
    override fun ticketLayout(): TicketLayout = KioskTicketLayout()

    override fun codeRenderer(): CodeRenderer = KioskCodeRenderer()

    override fun receiptLayout(): ReceiptLayout = KioskReceiptLayout()
}
