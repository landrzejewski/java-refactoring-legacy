package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale

/** Krok 2: stopka lojalnościowa - 1 punkt za każde pełne 10.00 za bilet, 100 punktów = darmowy bilet 2D. */
data class LoyaltyFooter(val inner: Document, val layout: ReceiptLayout, val sale: Sale) : Document {
    override fun render(): String {
        val earned = sale.ticketPrice.amount.toInt() / 10
        val total = sale.loyaltyPoints + earned
        val lines = mutableListOf<String>()
        lines += "Punkty: " + sale.loyaltyPoints + " + " + earned + " = " + total
        if (total >= 100) {
            lines += "Odbierz darmowy bilet 2D"
        }
        return inner.render() + "\n" + layout.section(lines)
    }
}
