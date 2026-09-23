package pl.training.workshop.m5.s01_pullupmethod.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: price() nadpisuje teraz metodę abstrakcyjną z Ticket (@Override). */
public final class VipTicket extends Ticket {
    public VipTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Override
    public Money price() {
        return basePrice().plus(Money.of("10.00"));
    }

    public String label() {
        return title() + ": " + price();
    }
}
