package pl.training.workshop.m6.s07_decorator.step2;

import pl.training.workshop.m6.s07_decorator.TicketOrder;
import pl.training.workshop.shared.Money;

/** Krok 2: flaga insurance usunięta z rdzenia - zajmuje się nią dekorator Insurance. */
public final class Ticket implements PricedTicket {
    private final String title;
    private final String format;
    private final Money base;
    private final boolean vip;
    private final boolean glasses;

    public Ticket(TicketOrder order) {
        this.title = order.title();
        this.format = order.format();
        this.base = order.base();
        this.vip = order.vip();
        this.glasses = order.format().equals("3D") && !order.ownGlasses();
    }

    @Override
    public Money price() {
        Money price = base;
        if (vip) {
            price = price.plus(Money.of("10.00"));
        }
        if (glasses) {
            price = price.plus(Money.of("3.00"));
        }
        return price;
    }

    @Override
    public String description() {
        return title + " " + format
                + (vip ? " +VIP" : "")
                + (glasses ? " +okulary 3D" : "");
    }
}
