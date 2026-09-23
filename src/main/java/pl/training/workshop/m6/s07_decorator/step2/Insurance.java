package pl.training.workshop.m6.s07_decorator.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: pierwszy dodatek jako dekorator. Zaczynamy od ubezpieczenia, bo w opisie jest
 * ostatnie - dekorator dopisuje się na końcu opisu obiektu, który owija.
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
