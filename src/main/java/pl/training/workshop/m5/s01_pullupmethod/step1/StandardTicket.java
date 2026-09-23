package pl.training.workshop.m5.s01_pullupmethod.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bez zmian - ta wersja label() jest wzorcem dla pozostałych. */
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
