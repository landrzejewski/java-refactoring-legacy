package pl.training.workshop.m5.s15_compatibility.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: bez zmian. */
public final class StandardTicket extends Ticket {
    public StandardTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Override
    protected int discountPercent() {
        return 0;
    }
}
