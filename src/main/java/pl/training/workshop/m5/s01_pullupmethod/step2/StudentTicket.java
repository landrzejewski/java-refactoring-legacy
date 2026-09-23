package pl.training.workshop.m5.s01_pullupmethod.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: price() nadpisuje teraz metodę abstrakcyjną z Ticket (@Override). */
public final class StudentTicket extends Ticket {
    public StudentTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Override
    public Money price() {
        return basePrice().minus(basePrice().percent(25));
    }

    public String label() {
        return title() + ": " + price();
    }
}
