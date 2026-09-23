package pl.training.workshop.m6.s07_decorator.step1;

import pl.training.workshop.m6.s07_decorator.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Extract Interface - Ticket implementuje PricedTicket, klienci zależą od interfejsu.
 * Flagi wciąż w środku; kontrakt jest gotowy na dekoratory.
 */
public final class Ticket implements PricedTicket {
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
