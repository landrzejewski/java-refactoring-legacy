package pl.training.workshop.m5.s01_pullupmethod.step2;

import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: Pull Members Up z opcją "Make abstract" dla {@code price()}.
 * Ciała zostają w podklasach (są różne), ale typ bazowy obiecuje cenę - to punkt rozszerzenia dla label().
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

    public abstract Money price();
}
