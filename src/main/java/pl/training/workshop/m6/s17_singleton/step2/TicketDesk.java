package pl.training.workshop.m6.s17_singleton.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: klient używa PriceList.INSTANCE - wciąż ukryta, globalna zależność. */
public final class TicketDesk {
    public Money quote(String format, boolean online) {
        Money price = PriceList.INSTANCE.basePrice(format);
        return online ? price.plus(Money.of("2.00")) : price;
    }
}
