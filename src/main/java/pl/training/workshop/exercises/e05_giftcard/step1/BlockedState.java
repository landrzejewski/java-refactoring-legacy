package pl.training.workshop.exercises.e05_giftcard.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: karta zablokowana - saldo zostaje, ale można ją tylko wygasić. */
public final class BlockedState implements CardState {
    public static final CardState INSTANCE = new BlockedState();

    private BlockedState() {
    }

    @Override
    public String name() {
        return "BLOCKED";
    }

    @Override
    public CardState activate() {
        throw new IllegalStateException("card is blocked");
    }

    @Override
    public CardState charge(GiftCard card, Money amount) {
        throw new IllegalStateException("card is blocked");
    }

    @Override
    public CardState block() {
        throw new IllegalStateException("card is blocked");
    }

    @Override
    public CardState expire() {
        return ExpiredState.INSTANCE;
    }
}
