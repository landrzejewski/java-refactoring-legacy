package pl.training.workshop.exercises.e05_giftcard.step1

import pl.training.workshop.shared.Money

/**
 * Krok 1: kontrakt stanu karty. Każde przejście zwraca stan następny albo rzuca ten sam wyjątek
 * z tym samym komunikatem co start. Stany są bezstanowe (saldo zostaje w karcie), więc każdy
 * ma jedną współdzieloną instancję (w Kotlinie: `object`).
 */
interface CardState {
    fun name(): String

    fun activate(): CardState

    fun charge(card: GiftCard, amount: Money): CardState

    fun block(): CardState

    fun expire(): CardState
}
