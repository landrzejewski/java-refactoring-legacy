package pl.training.workshop.m5.s01_pullupmethod.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: ujednolicenie ciała {@code label()} - teraz tekstowo identyczne jak w StandardTicket. */
public final class VipTicket extends Ticket {
    public VipTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    public Money price() {
        return basePrice().plus(Money.of("10.00"));
    }

    public String label() {
        return title() + ": " + price();
    }
}
