package pl.training.workshop.exercises.e07_documents.step2

import java.math.BigDecimal
import java.math.RoundingMode

import pl.training.workshop.exercises.e07_documents.Sale
import pl.training.workshop.shared.Money

/** Krok 2: sekcja VAT (bilety 8%, bar 23%) dopisana pod dokumentem wewnętrznym. */
data class VatSection(val inner: Document, val layout: ReceiptLayout, val sale: Sale) : Document {
    override fun render(): String {
        val lines = mutableListOf<String>()
        lines += "VAT 8% bilet: " + vat(sale.ticketPrice, 8)
        if (sale.barPrice.isGreaterThan(Money.ZERO)) {
            lines += "VAT 23% bar: " + vat(sale.barPrice, 23)
        }
        return inner.render() + "\n" + layout.section(lines)
    }

    private companion object {
        /** VAT zawarty w cenie brutto, np. 8% z 40.00 to 2.96. */
        fun vat(gross: Money, rate: Int): Money = Money(
            gross.amount
                .multiply(BigDecimal.valueOf(rate.toLong()))
                .divide(BigDecimal.valueOf((100 + rate).toLong()), 2, RoundingMode.HALF_UP)
        )
    }
}
