package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.shared.Money

/** Krok 2: karta zablokowana - saldo zostaje, ale można ją tylko wygasić. */
object BlockedState : CardState {
    override fun name(): String = "BLOCKED"

    override fun activate(): CardState {
        throw IllegalStateException("card is blocked")
    }

    override fun charge(card: GiftCard, amount: Money): CardState {
        throw IllegalStateException("card is blocked")
    }

    override fun block(): CardState {
        throw IllegalStateException("card is blocked")
    }

    override fun expire(): CardState = ExpiredState
}
