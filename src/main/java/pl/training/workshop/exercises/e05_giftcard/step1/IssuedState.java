package pl.training.workshop.exercises.e05_giftcard.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: karta wydana, jeszcze nieaktywna - można ją aktywować, zablokować albo wygasić. */
public final class IssuedState implements CardState {
    public static final CardState INSTANCE = new IssuedState();

    private IssuedState() {
    }

    @Override
    public String name() {
        return "ISSUED";
    }

    @Override
    public CardState activate() {
        return ActiveState.INSTANCE;
    }

    @Override
    public CardState charge(GiftCard card, Money amount) {
        throw new IllegalStateException("card not activated");
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
