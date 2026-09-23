package pl.training.workshop.m5.s03_pushdown.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: bez zmian - klient już pracuje na podtypie.
 */
public final class BoxOffice {
    public Money sell(String kind, Money basePrice, boolean vip) {
        if ("STUDENT".equals(kind)) {
            return new StudentTicket(basePrice).price();
        }
        StandardTicket ticket = new StandardTicket(basePrice);
        if (vip) {
            ticket.upgradeToVip();
        }
        return ticket.price();
    }
}
