package pl.training.workshop.m5.s09_overloading.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: zniżka studencka jako override - dyspozycja dynamiczna, a nie wybór kompilatora. */
public class StudentTicket extends Ticket {
    public StudentTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Override
    public int discountPercent() {
        return 25;
    }
}
