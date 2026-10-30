package pl.training.workshop.exercises.e09_cashdrawer.step1

import java.math.BigDecimal
import java.math.RoundingMode

import pl.training.workshop.exercises.e09_cashdrawer.DrawerDay
import pl.training.workshop.shared.Money

/**
 * Krok 1: trzy operacje na wpisach to trzy odwiedzający; raport tylko sumuje i skleja ich wyniki.
 * W klasie nie ma już `is`. Podział rabatu grupowego nadal łańcuchem if po trybie.
 */
class CashDrawerReport {
    private val cashEffect = CashEffectVisitor()
    private val fiscalLine = FiscalLineVisitor()
    private val auditLine = AuditLineVisitor()

    fun render(day: DrawerDay): String {
        val entries = day.lines.map(DrawerEntries::from)
        var cash = Money.ZERO
        val fiscal = StringBuilder()
        val audit = StringBuilder()
        for (entry in entries) {
            cash += entry.accept(cashEffect)
            val line = entry.accept(fiscalLine)
            if (line.isNotEmpty()) {
                fiscal.append(line).append('\n')
            }
            audit.append(entry.accept(auditLine)).append('\n')
        }
        return "Gotowka w szufladzie: $cash\n" +
            "Eksport fiskalny:\n$fiscal" +
            "Audyt:\n$audit" +
            groupDiscount(day.groupTickets, day.splitMode) + '\n'
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
