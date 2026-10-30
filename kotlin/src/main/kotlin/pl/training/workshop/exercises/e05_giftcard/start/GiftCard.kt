package pl.training.workshop.exercises.e05_giftcard.start

import pl.training.workshop.shared.Money

/**
 * Start: karta podarunkowa ze statusem w polu tekstowym (ISSUED, ACTIVE, EMPTY, BLOCKED, EXPIRED).
 * Każda operacja zaczyna się od `when` po statusie, więc tabela przejść jest rozproszona po
 * czterech metodach: nowy status (np. zawieszona) albo nowa operacja to zmiana w każdej z nich,
 * a komunikat "card is blocked" powtarza się w trzech miejscach.
 */
class GiftCard(initialBalance: Money) {
    private var status = "ISSUED"
    private var balance: Money

    init {
        if (!initialBalance.isGreaterThan(Money.ZERO)) {
            throw IllegalArgumentException("initial balance must be positive")
        }
        balance = initialBalance
    }

    fun activate() {
        when (status) {
            "ISSUED" -> status = "ACTIVE"
            "ACTIVE" -> throw IllegalStateException("card already active")
            "EMPTY" -> throw IllegalStateException("card is empty")
            "BLOCKED" -> throw IllegalStateException("card is blocked")
            "EXPIRED" -> throw IllegalStateException("card has expired")
            else -> throw IllegalStateException("unknown status: $status")
        }
    }

    fun charge(amount: Money) {
        if (!amount.isGreaterThan(Money.ZERO)) {
            throw IllegalArgumentException("amount must be positive")
        }
        when (status) {
            "ISSUED" -> throw IllegalStateException("card not activated")
            "ACTIVE" -> {
                if (amount.isGreaterThan(balance)) {
                    throw IllegalStateException("insufficient balance")
                }
                balance = balance.minus(amount)
                if (balance.compareTo(Money.ZERO) == 0) {
                    status = "EMPTY"
                }
            }
            "EMPTY" -> throw IllegalStateException("card is empty")
            "BLOCKED" -> throw IllegalStateException("card is blocked")
            "EXPIRED" -> throw IllegalStateException("card has expired")
            else -> throw IllegalStateException("unknown status: $status")
        }
    }

    fun block() {
        when (status) {
            "ISSUED", "ACTIVE" -> status = "BLOCKED"
            "EMPTY" -> throw IllegalStateException("card is empty")
            "BLOCKED" -> throw IllegalStateException("card is blocked")
            "EXPIRED" -> throw IllegalStateException("card has expired")
            else -> throw IllegalStateException("unknown status: $status")
        }
    }

    fun expire() {
        when (status) {
            "ISSUED", "ACTIVE", "EMPTY", "BLOCKED" -> status = "EXPIRED"
            "EXPIRED" -> throw IllegalStateException("card has expired")
            else -> throw IllegalStateException("unknown status: $status")
        }
    }

    fun status(): String = status

    fun balance(): Money = balance
}
