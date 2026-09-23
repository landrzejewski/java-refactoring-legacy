package pl.training.workshop.m5.s03_pushdown.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: bez zmian - klient był przygotowany w kroku 1.
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
