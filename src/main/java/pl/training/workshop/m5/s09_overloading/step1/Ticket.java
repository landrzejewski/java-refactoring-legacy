package pl.training.workshop.m5.s09_overloading.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: Replace Overloading with Overriding - zniżka to metoda instancji {@code discountPercent()},
 * wybierana dynamicznie według klasy runtime obiektu. Pułapka equals(Ticket) jeszcze zostaje.
 */
public class Ticket {
    private final String title;
    private final Money basePrice;

    public Ticket(String title, Money basePrice) {
        this.title = title;
        this.basePrice = basePrice;
    }

    public String title() {
        return title;
    }

    public Money basePrice() {
        return basePrice;
    }

    public int discountPercent() {
        return 0;
    }

    public boolean equals(Ticket other) {
        return title.equals(other.title) && basePrice.equals(other.basePrice);
    }
}
