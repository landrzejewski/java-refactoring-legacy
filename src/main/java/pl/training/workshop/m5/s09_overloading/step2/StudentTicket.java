package pl.training.workshop.m5.s09_overloading.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian. */
public class StudentTicket extends Ticket {
    public StudentTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Override
    public int discountPercent() {
        return 25;
    }
}
