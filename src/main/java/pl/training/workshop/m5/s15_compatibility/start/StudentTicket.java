package pl.training.workshop.m5.s15_compatibility.start;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s15_compatibility.Column;

/** Start: @Column("cena") zadeklarowane w podklasie (-25%). */
public final class StudentTicket extends Ticket {
    public StudentTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Column("cena")
    public Money price() {
        return basePrice().minus(basePrice().percent(25));
    }
}
