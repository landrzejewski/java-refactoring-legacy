package pl.training.workshop.exercises.e07_documents.step1

import java.math.BigDecimal
import java.math.RoundingMode

import pl.training.workshop.exercises.e07_documents.Channel
import pl.training.workshop.exercises.e07_documents.PrintOptions
import pl.training.workshop.exercises.e07_documents.Sale
import pl.training.workshop.shared.Money

/**
 * Krok 1: trzy `when` zastąpione jednym wyborem rodziny. Opcjonalne sekcje nadal
 * są łańcuchem if z obliczeniami w środku - to zostaje na krok 2.
 */
class SalesDocuments {
    fun print(sale: Sale?, channel: Channel?, options: PrintOptions?): String {
        if (sale == null) {
            throw IllegalArgumentException("sale must not be null")
        }
        if (channel == null) {
            throw IllegalArgumentException("channel must not be null")
        }
        if (options == null) {
            throw IllegalArgumentException("options must not be null")
        }
        if (sale.ticketPrice < Money.ZERO) {
            throw IllegalArgumentException("ticket price must not be negative")
        }
        if (sale.barPrice < Money.ZERO) {
            throw IllegalArgumentException("bar price must not be negative")
        }
        val family = DocumentFactories.forChannel(channel)
        val receiptLayout = family.receiptLayout()
        val document = StringBuilder(DocumentComposer(family).compose(sale))
        if (options.withVat) {
            val lines = mutableListOf<String>()
            lines += "VAT 8% bilet: " + vat(sale.ticketPrice, 8)
            if (sale.barPrice.isGreaterThan(Money.ZERO)) {
                lines += "VAT 23% bar: " + vat(sale.barPrice, 23)
            }
            document.append("\n").append(receiptLayout.section(lines))
        }
        if (options.withPromo) {
            document.append("\n").append(receiptLayout.section(listOf("Kod KINO10: -10% na nastepny bilet online")))
        }
        if (options.withLoyaltyFooter) {
            val earned = sale.ticketPrice.amount.toInt() / 10
            val total = sale.loyaltyPoints + earned
            val lines = mutableListOf<String>()
            lines += "Punkty: " + sale.loyaltyPoints + " + " + earned + " = " + total
            if (total >= 100) {
                lines += "Odbierz darmowy bilet 2D"
            }
            document.append("\n").append(receiptLayout.section(lines))
        }
        return document.toString()
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
