package pl.training.workshop.m5.s09_overloading.start;

import pl.training.workshop.shared.Money;

/**
 * Start: stan po Extract Superclass (wcześniej StandardTicket i StudentTicket były niezależne).
 * Pułapka 2: {@code equals(Ticket)} to PRZECIĄŻENIE, nie nadpisanie equals(Object) - kolekcje go nie widzą.
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

    public boolean equals(Ticket other) {
        return title.equals(other.title) && basePrice.equals(other.basePrice);
    }
}
