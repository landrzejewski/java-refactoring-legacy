package pl.training.workshop.exercises.e09_cashdrawer.step2

import java.math.BigDecimal
import java.math.RoundingMode

import pl.training.workshop.shared.Money

/**
 * Krok 2: równe części zaokrąglone w dół; brakujące grosze dostają po jednym pierwsze bilety.
 * Bezstanowy.
 */
class EqualSplit : DiscountSplit {
    override fun split(discount: Money, tickets: List<Money>): List<Money> {
        val base = Money(
            discount.amount
                .divide(BigDecimal.valueOf(tickets.size.toLong()), 2, RoundingMode.DOWN)
        )
        val restCents = (discount - base * tickets.size).amount.movePointRight(2).intValueExact()
        val shares = mutableListOf<Money>()
        for (i in 0 until tickets.size) {
            shares += if (i < restCents) base + CENT else base
        }
        return shares
    }

    private companion object {
        val CENT = Money.of("0.01")
    }
}
