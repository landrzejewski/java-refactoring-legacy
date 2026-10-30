package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.shared.Money

/** Krok 2: klient karty - bez zmian (skrypt operacji kasy). */
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
