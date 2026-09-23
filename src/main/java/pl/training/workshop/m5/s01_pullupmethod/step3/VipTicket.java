package pl.training.workshop.m5.s01_pullupmethod.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: w podklasie zostaje tylko to, co naprawdę różne - cena (+10.00 za VIP). */
public final class VipTicket extends Ticket {
    public VipTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Override
    public Money price() {
        return basePrice().plus(Money.of("10.00"));
    }
}
