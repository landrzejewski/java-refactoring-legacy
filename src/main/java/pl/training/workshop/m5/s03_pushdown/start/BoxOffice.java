package pl.training.workshop.m5.s03_pushdown.start;

import pl.training.workshop.shared.Money;

/** Start: klient pracuje na typie bazowym i sprawdza instanceof, zanim wywoła upgradeToVip(). */
public final class BoxOffice {
    public Money sell(String kind, Money basePrice, boolean vip) {
        Ticket ticket = "STUDENT".equals(kind)
                ? new StudentTicket(basePrice)
                : new StandardTicket(basePrice);
        if (vip && ticket instanceof StandardTicket) {
            ticket.upgradeToVip();
        }
        return ticket.price();
    }
}
