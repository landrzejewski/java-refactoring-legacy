package pl.training.workshop.m6.s07_decorator.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: dopłata za miejsce VIP jako dekorator. */
public record VipSeat(PricedTicket inner) implements PricedTicket {
    @Override
    public Money price() {
        return inner.price().plus(Money.of("10.00"));
    }

    @Override
    public String description() {
        return inner.description() + " +VIP";
    }
}
