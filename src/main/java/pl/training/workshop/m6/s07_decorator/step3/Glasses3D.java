package pl.training.workshop.m6.s07_decorator.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: okulary 3D jako dekorator; decyzja "czy potrzebne" została w fabryce. */
public record Glasses3D(PricedTicket inner) implements PricedTicket {
    @Override
    public Money price() {
        return inner.price().plus(Money.of("3.00"));
    }

    @Override
    public String description() {
        return inner.description() + " +okulary 3D";
    }
}
