package pl.training.workshop.exercises.e07_documents.start

import pl.training.workshop.exercises.e07_documents.Sale

/**
 * Start: skleja trzy części dokumentu. Przyjmuje je osobno, więc nic nie broni
 * złożyć biletu kioskowego z kodem QR aplikacji i paragonem A4 - kompilator tego nie zauważy.
 */
class DocumentComposer(
    private val ticketLayout: TicketLayout,
    private val codeRenderer: CodeRenderer,
    private val receiptLayout: ReceiptLayout
) {
    fun compose(sale: Sale): String = ticketLayout.ticket(sale) + "\n" + codeRenderer.code(sale) + "\n" + receiptLayout.receipt(sale)
}
