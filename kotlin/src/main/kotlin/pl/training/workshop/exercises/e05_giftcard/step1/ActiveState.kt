package pl.training.workshop.exercises.e05_giftcard.step1

import pl.training.workshop.shared.Money

/** Krok 1: karta aktywna - jedyny stan, w którym obciążenie zmienia saldo; przy zerze przechodzi w EMPTY. */
object ActiveState : CardState {
    override fun name(): String = "ACTIVE"

    override fun activate(): CardState {
        throw IllegalStateException("card already active")
    }

    override fun charge(card: GiftCard, amount: Money): CardState {
        if (amount.isGreaterThan(card.balance())) {
            throw IllegalStateException("insufficient balance")
        }
        card.debit(amount)
        return if (card.balance().compareTo(Money.ZERO) == 0) EmptyState else this
    }

    override fun block(): CardState = BlockedState

    override fun expire(): CardState = ExpiredState
}
