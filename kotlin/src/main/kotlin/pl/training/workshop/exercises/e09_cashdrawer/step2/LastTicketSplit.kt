package pl.training.workshop.exercises.e09_cashdrawer.step2

import pl.training.workshop.shared.Money

/** Krok 2: cały rabat na ostatnim bilecie - bez zaokrągleń. Bezstanowy. */
class LastTicketSplit : DiscountSplit {
    override fun split(discount: Money, tickets: List<Money>): List<Money> {
        val shares = mutableListOf<Money>()
        for (i in 0 until tickets.size - 1) {
            shares += Money.ZERO
        }
        shares += discount
        return shares
    }
}
