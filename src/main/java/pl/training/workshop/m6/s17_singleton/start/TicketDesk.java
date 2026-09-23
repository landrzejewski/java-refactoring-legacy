package pl.training.workshop.m6.s17_singleton.start;

import pl.training.workshop.shared.Money;

/** Start: każda wycena tworzy i parsuje nowy cennik. */
public final class TicketDesk {
    public Money quote(String format, boolean online) {
        Money price = new PriceList().basePrice(format);
        return online ? price.plus(Money.of("2.00")) : price;
    }
}
