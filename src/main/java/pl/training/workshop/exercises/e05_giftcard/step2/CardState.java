package pl.training.workshop.exercises.e05_giftcard.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: kontrakt stanu karty. Każde przejście zwraca stan następny albo rzuca ten sam wyjątek
 * z tym samym komunikatem co start. Stany są bezstanowe (saldo zostaje w karcie), więc każdy
 * ma jedną współdzieloną instancję.
 */
public interface CardState {
    String name();

    CardState activate();

    CardState charge(GiftCard card, Money amount);

    CardState block();

    CardState expire();
}
