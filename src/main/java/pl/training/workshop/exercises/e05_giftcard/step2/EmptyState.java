package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: karta wyczerpana - jedyne dozwolone przejście to wygaśnięcie. */
public final class EmptyState implements CardState {
    public static final CardState INSTANCE = new EmptyState();

    private EmptyState() {
    }

    @Override
    public String name() {
        return "EMPTY";
    }

    @Override
    public CardState activate() {
        throw new IllegalStateException("card is empty");
    }

    @Override
    public CardState charge(GiftCard card, Money amount) {
        throw new IllegalStateException("card is empty");
    }

    @Override
    public CardState block() {
        throw new IllegalStateException("card is empty");
    }

    @Override
    public CardState expire() {
        return ExpiredState.INSTANCE;
    }
}
