package pl.training.workshop.m5.s01_pullupmethod.step1;

import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: bez zmian - nadklasa nadal nie zna ceny ani etykiety.
 * Każda podklasa ma własne {@code price()} i własne {@code label()} - trzy teksty, jedno zachowanie.
 */
public abstract class Ticket {
    private final String title;
    private final Money basePrice;

    protected Ticket(String title, Money basePrice) {
        this.title = Objects.requireNonNull(title, "title");
        this.basePrice = Objects.requireNonNull(basePrice, "basePrice");
    }

    public String title() {
        return title;
    }

    public Money basePrice() {
        return basePrice;
    }
}
