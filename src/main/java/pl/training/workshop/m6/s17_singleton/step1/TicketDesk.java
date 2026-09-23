package pl.training.workshop.m6.s17_singleton.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: klient pyta o jedyną instancję zamiast robić new. */
public final class TicketDesk {
    public Money quote(String format, boolean online) {
        Money price = PriceList.getInstance().basePrice(format);
        return online ? price.plus(Money.of("2.00")) : price;
    }
}
