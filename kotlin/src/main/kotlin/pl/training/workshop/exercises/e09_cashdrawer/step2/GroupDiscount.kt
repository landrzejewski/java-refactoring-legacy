package pl.training.workshop.exercises.e09_cashdrawer.step2

import pl.training.workshop.shared.Money

/**
 * Krok 2: reguła rabatu grupowego (10+ biletów = -10% sumy) jako kontekst, który podział
 * rabatu deleguje do wstrzykniętego wariantu. Kontekst nie zna nazw trybów.
 */
class GroupDiscount(private val split: DiscountSplit) {
    /** Rabat i jego podział na bilety; `null`, gdy grupa jest za mała. */
    fun allocate(tickets: List<Money>): Allocation? {
        if (tickets.size < GROUP_SIZE) {
            return null
        }
        var sum = Money.ZERO
        for (ticket in tickets) {
            sum += ticket
        }
        val discount = sum.percent(GROUP_PERCENT)
        return Allocation(discount, split.split(discount, tickets))
    }

    data class Allocation(val discount: Money, val shares: List<Money>)

    private companion object {
        const val GROUP_SIZE = 10
        const val GROUP_PERCENT = 10
    }
}
