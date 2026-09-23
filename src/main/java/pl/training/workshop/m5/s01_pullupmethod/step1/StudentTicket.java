package pl.training.workshop.m5.s01_pullupmethod.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: ujednolicenie ciała {@code label()} - teraz tekstowo identyczne jak w StandardTicket. */
public final class StudentTicket extends Ticket {
    public StudentTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    public Money price() {
        return basePrice().minus(basePrice().percent(25));
    }

    public String label() {
        return title() + ": " + price();
    }
}
