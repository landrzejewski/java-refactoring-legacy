package pl.training.workshop.exercises.e05_giftcard.step1;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 1: klient karty - bez zmian (skrypt operacji kasy). */
public final class GiftCards {
    private GiftCards() {
    }

    public static String simulate(Money initialBalance, List<String> operations) {
        GiftCard card = new GiftCard(initialBalance);
        for (String operation : operations) {
            String[] parts = operation.trim().split(" ");
            switch (parts[0]) {
                case "activate" -> card.activate();
                case "charge" -> card.charge(Money.of(parts[1]));
                case "block" -> card.block();
                case "expire" -> card.expire();
                default -> throw new IllegalArgumentException("unknown operation: " + operation);
            }
        }
        return card.status() + " " + card.balance();
    }
}
