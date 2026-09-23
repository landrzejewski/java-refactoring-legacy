package pl.training.workshop.m5.s01_pullupmethod.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: w podklasie zostaje tylko to, co naprawdę różne - cena. */
public final class StandardTicket extends Ticket {
    public StandardTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Override
    public Money price() {
        return basePrice();
    }
}
