package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: karta wygasła - stan końcowy, każda operacja kończy się tym samym błędem. */
public final class ExpiredState implements CardState {
    public static final CardState INSTANCE = new ExpiredState();

    private ExpiredState() {
    }

    @Override
    public String name() {
        return "EXPIRED";
    }

    @Override
    public CardState activate() {
        throw new IllegalStateException("card has expired");
    }

    @Override
    public CardState charge(GiftCard card, Money amount) {
        throw new IllegalStateException("card has expired");
    }

    @Override
    public CardState block() {
        throw new IllegalStateException("card has expired");
    }

    @Override
    public CardState expire() {
        throw new IllegalStateException("card has expired");
    }
}
