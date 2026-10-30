package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Sale
import pl.training.workshop.shared.Money

/** Krok 2: paragon w aplikacji mobilnej. */
internal class MobileReceiptLayout : ReceiptLayout {
    override fun receipt(sale: Sale): String {
        val out = StringBuilder("<receipt>")
        out.append("<li>Bilet ").append(sale.ticketPrice).append("</li>")
        if (sale.barPrice.isGreaterThan(Money.ZERO)) {
            out.append("<li>Bar ").append(sale.barPrice).append("</li>")
        }
        out.append("<li><b>Razem ").append(sale.ticketPrice.plus(sale.barPrice)).append("</b></li>")
        return out.append("</receipt>").toString()
    }

    override fun section(lines: List<String>): String = lines.joinToString("</p><p>", "<section><p>", "</p></section>")
}
