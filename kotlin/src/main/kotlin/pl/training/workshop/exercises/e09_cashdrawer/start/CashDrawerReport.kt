package pl.training.workshop.exercises.e09_cashdrawer.start

import java.math.BigDecimal
import java.math.RoundingMode

import pl.training.workshop.exercises.e09_cashdrawer.DrawerDay
import pl.training.workshop.shared.Money

/**
 * Start: raport zamknięcia kasy. Trzy operacje na wpisach szuflady (wpływ na gotówkę, linia
 * eksportu fiskalnego, linia audytu) to trzy łańcuchy `is` zakończone wyjątkiem w runtime:
 * nowy rodzaj wpisu kompiluje się bez błędu i wybucha przy zamknięciu dnia. Podział rabatu
 * grupowego na bilety wybiera łańcuch if po nazwie trybu z konfiguracji, wymieszany z regułą
 * "10+ biletów = -10%" i z poprawką zaokrąglenia, która musi dać dokładnie kwotę rabatu.
 */
class CashDrawerReport {
    fun render(day: DrawerDay): String {
        val entries = day.lines.map(DrawerEntries::from)
        var cash = Money.ZERO
        val fiscal = StringBuilder()
        val audit = StringBuilder()
        for (entry in entries) {
            cash += cashEffect(entry)
            val fiscalLine = fiscalLine(entry)
            if (fiscalLine.isNotEmpty()) {
                fiscal.append(fiscalLine).append('\n')
            }
            audit.append(auditLine(entry)).append('\n')
        }
        return "Gotowka w szufladzie: $cash\n" +
            "Eksport fiskalny:\n$fiscal" +
            "Audyt:\n$audit" +
            groupDiscount(day.groupTickets, day.splitMode) + '\n'
    }

    private fun cashEffect(entry: DrawerEntry): Money = when (entry) {
        is CashSale -> entry.amount
        is CardSale -> Money.ZERO
        is Refund -> Money.ZERO - entry.amount
        is Deposit -> entry.amount
        else -> throw IllegalArgumentException("unknown entry: $entry")
    }

    /** Format drukarki fiskalnej: rodzaj;numer;kwota;stawka VAT[;karta]. Pogotowie kasowe nie jest fiskalne. */
    private fun fiscalLine(entry: DrawerEntry): String = when (entry) {
        is CashSale -> "S;${entry.reference};${entry.amount};8"
        is CardSale -> "K;${entry.reference};${entry.amount};8;${entry.cardLast4}"
        is Refund -> "Z;${entry.reference};-${entry.amount};8"
        is Deposit -> ""
        else -> throw IllegalArgumentException("unknown entry: $entry")
    }

    private fun auditLine(entry: DrawerEntry): String = when (entry) {
        is CashSale -> "CASH ${entry.reference} ${entry.amount}"
        is CardSale -> "CARD ${entry.reference} ${entry.amount} karta ****${entry.cardLast4}"
        is Refund -> "REFUND ${entry.reference} -${entry.amount} powod: ${entry.reason}"
        is Deposit -> "DEPOSIT ${entry.amount} ${entry.note}"
        else -> throw IllegalArgumentException("unknown entry: $entry")
    }

    private fun groupDiscount(tickets: List<Money>, mode: String): String {
        if (tickets.size < GROUP_SIZE) {
            return "Rabat grupowy: brak (${tickets.size} biletow)"
        }
        var sum = Money.ZERO
        for (ticket in tickets) {
            sum += ticket
        }
        val discount = sum.percent(GROUP_PERCENT)
        val shares = mutableListOf<Money>()
        if (mode == "PROPORTIONAL") {
            var given = Money.ZERO
            for (i in 0 until tickets.size - 1) {
                val share = Money(
                    discount.amount.multiply(tickets[i].amount)
                        .divide(sum.amount, 2, RoundingMode.HALF_UP)
                )
                shares += share
                given += share
            }
            shares += discount - given
        } else if (mode == "LAST") {
            for (i in 0 until tickets.size - 1) {
                shares += Money.ZERO
            }
            shares += discount
        } else if (mode == "EQUAL") {
            val base = Money(
                discount.amount
                    .divide(BigDecimal.valueOf(tickets.size.toLong()), 2, RoundingMode.DOWN)
            )
            val restCents = (discount - base * tickets.size).amount.movePointRight(2).intValueExact()
            for (i in 0 until tickets.size) {
                shares += if (i < restCents) base + CENT else base
            }
        } else {
            throw IllegalArgumentException("unknown split mode: $mode")
        }
        return "Rabat grupowy $mode: $discount = " + shares.joinToString(" + ")
    }

    private companion object {
        const val GROUP_SIZE = 10
        const val GROUP_PERCENT = 10
        val CENT = Money.of("0.01")
    }
}
