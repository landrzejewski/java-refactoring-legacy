package pl.training.workshop.m6.s15_command.step2;

import java.util.Map;

import pl.training.workshop.shared.Money;

/** Krok 2: stan kasy wydzielony z konsoli - komendy dostają go jako argument i same są bezstanowe. */
public final class Till {
    private static final Map<String, Money> PRICES = Map.of(
            "Diuna", Money.of("40.00"),
            "Kraina Lodu", Money.of("32.00"),
            "Amator", Money.of("25.00"));

    private Money cash = Money.ZERO;
    private int tickets;

    Money priceOf(String title) {
        return PRICES.get(title);
    }

    void sold(int quantity, Money total) {
        cash = cash.plus(total);
        tickets += quantity;
    }

    void refunded(Money price) {
        cash = cash.minus(price);
        tickets--;
    }

    Money cash() {
        return cash;
    }

    int tickets() {
        return tickets;
    }
}
