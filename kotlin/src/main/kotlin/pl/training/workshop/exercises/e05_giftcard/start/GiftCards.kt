package pl.training.workshop.exercises.e05_giftcard.start

import pl.training.workshop.shared.Money

/**
 * Start: klient karty - kasa wykonuje skrypt operacji (activate, charge <kwota>, block, expire)
 * i raportuje status oraz saldo. Przez niego woła test.
 */
object GiftCards {
    fun simulate(initialBalance: Money, operations: List<String>): String {
        val card = GiftCard(initialBalance)
        for (operation in operations) {
            val parts = operation.trim().split(" ")
            when (parts[0]) {
                "activate" -> card.activate()
                "charge" -> card.charge(Money.of(parts[1]))
                "block" -> card.block()
                "expire" -> card.expire()
                else -> throw IllegalArgumentException("unknown operation: $operation")
            }
        }
        return card.status() + " " + card.balance()
    }
}
