package pl.training.workshop.m5.s15_compatibility.step1;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s15_compatibility.Column;

/** Krok 1: bez zmian. */
public final class StandardTicket extends Ticket {
    public StandardTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Column("cena")
    public Money price() {
        return basePrice();
    }
}
