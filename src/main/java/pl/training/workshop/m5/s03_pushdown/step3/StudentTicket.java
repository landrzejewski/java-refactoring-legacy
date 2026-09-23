package pl.training.workshop.m5.s03_pushdown.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: override rzucający UnsupportedOperationException zniknął - nie ma czego odmawiać. */
public final class StudentTicket extends Ticket {
    public StudentTicket(Money basePrice) {
        super(basePrice);
    }

    @Override
    protected int discountPercent() {
        return 25;
    }
}
