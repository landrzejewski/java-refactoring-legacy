package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: karta trzyma obiekt stanu zamiast tekstu i tylko do niego deleguje. Wspólna walidacja
 * (saldo początkowe, dodatnia kwota) zostaje w karcie, bo nie zależy od stanu. Tabela przejść
 * jest teraz w klasach stanów: jeden stan = jedna klasa, jeden wiersz tabeli.
 */
public final class GiftCard {
    private CardState state = IssuedState.INSTANCE;
    private Money balance;

    public GiftCard(Money initialBalance) {
        if (!initialBalance.isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("initial balance must be positive");
        }
        this.balance = initialBalance;
    }

    public void activate() {
        state = state.activate();
    }

    public void charge(Money amount) {
        if (!amount.isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("amount must be positive");
        }
        state = state.charge(this, amount);
    }

    public void block() {
        state = state.block();
    }

    public void expire() {
        state = state.expire();
    }

    /** Dostępne tylko dla stanów z tego pakietu - stan aktywny zmniejsza saldo. */
    void debit(Money amount) {
        balance = balance.minus(amount);
    }

    public String status() {
        return state.name();
    }

    public Money balance() {
        return balance;
    }
}
