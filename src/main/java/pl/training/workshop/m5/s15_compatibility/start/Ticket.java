package pl.training.workshop.m5.s15_compatibility.start;

import pl.training.workshop.shared.Money;

/** Start: baza biletów bez ceny - każda podklasa deklaruje własne price() z adnotacją @Column. */
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
