package pl.training.workshop.exercises.e09_cashdrawer.step2

import java.math.RoundingMode

import pl.training.workshop.shared.Money

/**
 * Krok 2: udział proporcjonalny do ceny biletu, zaokrąglony HALF_UP; różnica zaokrągleń
 * trafia na ostatni bilet, żeby suma zgadzała się co do grosza. Bezstanowy.
 */
class ProportionalSplit : DiscountSplit {
    override fun split(discount: Money, tickets: List<Money>): List<Money> {
        var sum = Money.ZERO
        for (ticket in tickets) {
            sum += ticket
        }
        val shares = mutableListOf<Money>()
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
        return shares
    }
}
