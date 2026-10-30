package pl.training.workshop.exercises.e05_giftcard.step2

import pl.training.workshop.shared.Money

/**
 * Krok 2: karta trzyma obiekt stanu zamiast tekstu i tylko do niego deleguje. Wspólna walidacja
 * (saldo początkowe, dodatnia kwota) zostaje w karcie, bo nie zależy od stanu. Tabela przejść
 * jest teraz w klasach stanów: jeden stan = jedna klasa, jeden wiersz tabeli.
 */
class GiftCard(initialBalance: Money) {
    private var state: CardState = IssuedState
    private var balance: Money

    init {
        if (!initialBalance.isGreaterThan(Money.ZERO)) {
            throw IllegalArgumentException("initial balance must be positive")
        }
        balance = initialBalance
    }

    fun activate() {
        state = state.activate()
    }

    fun charge(amount: Money) {
        if (!amount.isGreaterThan(Money.ZERO)) {
            throw IllegalArgumentException("amount must be positive")
        }
        state = state.charge(this, amount)
    }

    fun block() {
        state = state.block()
    }

    fun expire() {
        state = state.expire()
    }

    /** Dostępne tylko dla stanów z tego modułu (w Javie: z tego pakietu) - stan aktywny zmniejsza saldo. */
    internal fun debit(amount: Money) {
        balance = balance.minus(amount)
    }

    fun status(): String = state.name()

    fun balance(): Money = balance
}
