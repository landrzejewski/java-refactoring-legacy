package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale
import pl.training.workshop.shared.Money

/** Krok 2: paragon kiosku - kwoty wyrównane do prawej w 32 kolumnach. */
internal class KioskReceiptLayout : ReceiptLayout {
    override fun receipt(sale: Sale): String {
        val lines = mutableListOf<String>()
        lines += "PARAGON"
        lines += row("Bilet", sale.ticketPrice)
        if (sale.barPrice.isGreaterThan(Money.ZERO)) {
            lines += row("Bar", sale.barPrice)
        }
        lines += row("RAZEM", sale.ticketPrice.plus(sale.barPrice))
        lines += KioskTicketLayout.RULE
        return lines.joinToString("\n")
    }

    override fun section(lines: List<String>): String = lines.joinToString("\n") + "\n" + "-".repeat(32)

    private fun row(label: String, amount: Money): String = "%-22s%10s".format(label, amount)
}
