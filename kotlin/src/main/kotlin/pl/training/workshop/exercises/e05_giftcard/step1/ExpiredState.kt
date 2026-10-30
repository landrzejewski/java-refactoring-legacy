package pl.training.workshop.exercises.e05_giftcard.step1

import pl.training.workshop.shared.Money

/** Krok 1: karta wygasła - stan końcowy, każda operacja kończy się tym samym błędem. */
object ExpiredState : CardState {
    override fun name(): String = "EXPIRED"

    override fun activate(): CardState {
        throw IllegalStateException("card has expired")
    }

    override fun charge(card: GiftCard, amount: Money): CardState {
        throw IllegalStateException("card has expired")
    }

    override fun block(): CardState {
        throw IllegalStateException("card has expired")
    }

    override fun expire(): CardState {
        throw IllegalStateException("card has expired")
    }
}
