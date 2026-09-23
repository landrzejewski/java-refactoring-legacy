package pl.training.workshop.m6.s07_decorator.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: bez zmian - ubezpieczenie jest zawsze najbardziej zewnętrzne,
 * bo w opisie występuje na końcu.
 */
public record Insurance(PricedTicket inner) implements PricedTicket {
    @Override
    public Money price() {
        return inner.price().plus(Money.of("4.00"));
    }

    @Override
    public String description() {
        return inner.description() + " +ubezpieczenie";
    }
}
