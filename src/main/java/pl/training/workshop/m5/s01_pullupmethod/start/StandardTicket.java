package pl.training.workshop.m5.s01_pullupmethod.start;

import pl.training.workshop.shared.Money;

/** Start: bilet normalny - etykieta sklejana operatorem +. */
public final class StandardTicket extends Ticket {
    public StandardTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    public Money price() {
        return basePrice();
    }

    public String label() {
        return title() + ": " + price();
    }
}
