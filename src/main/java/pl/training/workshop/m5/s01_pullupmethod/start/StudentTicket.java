package pl.training.workshop.m5.s01_pullupmethod.start;

import pl.training.workshop.shared.Money;

/** Start: bilet studencki (-25%) - ta sama etykieta, ale przez String.format. */
public final class StudentTicket extends Ticket {
    public StudentTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    public Money price() {
        return basePrice().minus(basePrice().percent(25));
    }

    public String label() {
        return String.format("%s: %s", title(), price());
    }
}
