package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.shared.Money

/** Krok 2: karta wydana, jeszcze nieaktywna - można ją aktywować, zablokować albo wygasić. */
object IssuedState : CardState {
    override fun name(): String = "ISSUED"

    override fun activate(): CardState = ActiveState

    override fun charge(card: GiftCard, amount: Money): CardState {
        throw IllegalStateException("card not activated")
    }

    override fun block(): CardState = BlockedState

    override fun expire(): CardState = ExpiredState
}
