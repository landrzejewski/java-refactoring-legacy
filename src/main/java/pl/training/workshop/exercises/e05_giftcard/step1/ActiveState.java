package pl.training.workshop.exercises.e05_giftcard.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: karta aktywna - jedyny stan, w którym obciążenie zmienia saldo; przy zerze przechodzi w EMPTY. */
public final class ActiveState implements CardState {
    public static final CardState INSTANCE = new ActiveState();

    private ActiveState() {
    }

    @Override
    public String name() {
        return "ACTIVE";
    }

    @Override
    public CardState activate() {
        throw new IllegalStateException("card already active");
    }

    @Override
    public CardState charge(GiftCard card, Money amount) {
        if (amount.isGreaterThan(card.balance())) {
            throw new IllegalStateException("insufficient balance");
        }
        card.debit(amount);
        return card.balance().compareTo(Money.ZERO) == 0 ? EmptyState.INSTANCE : this;
    }

    @Override
    public CardState block() {
        return BlockedState.INSTANCE;
    }

    @Override
    public CardState expire() {
        return ExpiredState.INSTANCE;
    }
}
