package pl.training.workshop.m6.s07_decorator.start;

import pl.training.workshop.m6.s07_decorator.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Start: rdzeń biletu obrośnięty flagami dodatków. Każdy nowy dodatek to kolejne pole,
 * kolejny if w price() i description() - a większość biletów nie ma żadnego dodatku.
 */
public final class Ticket {
    private final String title;
    private final String format;
    private final Money base;
    private final boolean vip;
    private final boolean glasses;
    private final boolean insurance;

    public Ticket(TicketOrder order) {
        this.title = order.title();
        this.format = order.format();
        this.base = order.base();
        this.vip = order.vip();
        this.glasses = order.format().equals("3D") && !order.ownGlasses();
        this.insurance = order.insurance();
    }

    public Money price() {
        Money price = base;
        if (vip) {
            price = price.plus(Money.of("10.00"));
        }
        if (glasses) {
            price = price.plus(Money.of("3.00"));
        }
        if (insurance) {
            price = price.plus(Money.of("4.00"));
        }
        return price;
    }

    public String description() {
        return title + " " + format
                + (vip ? " +VIP" : "")
                + (glasses ? " +okulary 3D" : "")
                + (insurance ? " +ubezpieczenie" : "");
    }
}
