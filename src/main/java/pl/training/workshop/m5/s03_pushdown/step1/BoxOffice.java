package pl.training.workshop.m5.s03_pushdown.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: klienci na podtyp - upgradeToVip() wołamy na zmiennej typu StandardTicket.
 * Po tym kroku nikt nie woła metody przez typ bazowy, więc można ją przesunąć w dół.
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
