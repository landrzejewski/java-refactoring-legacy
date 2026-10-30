package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale
import pl.training.workshop.shared.Money

/** Krok 2: paragon fiskalny A4 z wykropkowaniem do kwoty. */
internal class BoxOfficeReceiptLayout : ReceiptLayout {
    override fun receipt(sale: Sale): String {
        val lines = mutableListOf<String>()
        lines += "PARAGON FISKALNY"
        lines += row("Bilet", sale.ticketPrice)
        if (sale.barPrice.isGreaterThan(Money.ZERO)) {
            lines += row("Bar", sale.barPrice)
        }
        lines += row("RAZEM", sale.ticketPrice.plus(sale.barPrice))
        return lines.joinToString("\n")
    }

    override fun section(lines: List<String>): String = lines.joinToString("\n") { line -> "  $line" }

    private fun row(label: String, amount: Money): String = label + " " + ".".repeat(18 - label.length) + " " + amount
}
