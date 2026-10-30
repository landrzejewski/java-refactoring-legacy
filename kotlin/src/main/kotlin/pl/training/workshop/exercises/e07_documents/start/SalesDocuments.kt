package pl.training.workshop.exercises.e07_documents.start

import java.math.BigDecimal
import java.math.RoundingMode

import pl.training.workshop.exercises.e07_documents.Channel
import pl.training.workshop.exercises.e07_documents.PrintOptions
import pl.training.workshop.exercises.e07_documents.Sale
import pl.training.workshop.shared.Money

/**
 * Start: punkt wejścia drukowania dokumentów sprzedaży. Trzy równoległe `when` po kanale
 * wybierają po jednym elemencie zestawu (bilet, kod, paragon); nowy kanał to trzy nowe gałęzie,
 * a pomylenie jednej z nich kompiluje się bez ostrzeżenia. Dalej każda opcjonalna sekcja
 * (VAT, promocja, stopka lojalnościowa) to kolejny if z obliczeniami w środku tej samej metody.
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
        val ticketLayout = ticketLayoutFor(channel)
        val codeRenderer = codeRendererFor(channel)
        val receiptLayout = receiptLayoutFor(channel)
        val document = StringBuilder(DocumentComposer(ticketLayout, codeRenderer, receiptLayout).compose(sale))
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

        fun ticketLayoutFor(channel: Channel): TicketLayout = when (channel) {
            Channel.KIOSK -> KioskTicketLayout()
            Channel.MOBILE -> MobileTicketLayout()
            Channel.BOX_OFFICE -> BoxOfficeTicketLayout()
        }

        fun codeRendererFor(channel: Channel): CodeRenderer = when (channel) {
            Channel.KIOSK -> KioskCodeRenderer()
            Channel.MOBILE -> MobileCodeRenderer()
            Channel.BOX_OFFICE -> BoxOfficeCodeRenderer()
        }

        fun receiptLayoutFor(channel: Channel): ReceiptLayout = when (channel) {
            Channel.KIOSK -> KioskReceiptLayout()
            Channel.MOBILE -> MobileReceiptLayout()
            Channel.BOX_OFFICE -> BoxOfficeReceiptLayout()
        }
    }
}
