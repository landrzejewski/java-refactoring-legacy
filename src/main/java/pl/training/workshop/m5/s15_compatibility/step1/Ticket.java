package pl.training.workshop.m5.s15_compatibility.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bez zmian. */
public abstract class Ticket {
    private final String title;
    private final Money basePrice;

    protected Ticket(String title, Money basePrice) {
        this.title = title;
        this.basePrice = basePrice;
    }

    public String title() {
        return title;
    }

    public Money basePrice() {
        return basePrice;
    }
}
