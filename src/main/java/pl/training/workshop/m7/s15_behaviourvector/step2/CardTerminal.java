package pl.training.workshop.m7.s15_behaviourvector.step2;

import pl.training.workshop.shared.Money;

/**
 * Statyczny terminal płatniczy. Karty kończące się na 0000 są odrzucane.
 * Obciążeń nie da się podejrzeć z testu.
 */
public final class CardTerminal {
    private CardTerminal() {
    }

    public static boolean charge(String card, Money amount) {
        return !card.endsWith("0000");
    }
}
