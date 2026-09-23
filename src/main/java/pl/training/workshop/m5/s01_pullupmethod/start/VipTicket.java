package pl.training.workshop.m5.s01_pullupmethod.start;

import pl.training.workshop.shared.Money;

/** Start: bilet na miejsce VIP (+10.00) - ta sama etykieta, ale przez StringBuilder. */
public final class VipTicket extends Ticket {
    public VipTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    public Money price() {
        return basePrice().plus(Money.of("10.00"));
    }

    public String label() {
        return new StringBuilder(title()).append(": ").append(price()).toString();
    }
}
