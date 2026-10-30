package pl.training.workshop.exercises.e05_giftcard.step1

import pl.training.workshop.shared.Money

/** Krok 1: karta wyczerpana - jedyne dozwolone przejście to wygaśnięcie. */
object EmptyState : CardState {
    override fun name(): String = "EMPTY"

    override fun activate(): CardState {
        throw IllegalStateException("card is empty")
    }

    override fun charge(card: GiftCard, amount: Money): CardState {
        throw IllegalStateException("card is empty")
    }

    override fun block(): CardState {
        throw IllegalStateException("card is empty")
    }

    override fun expire(): CardState = ExpiredState
}
