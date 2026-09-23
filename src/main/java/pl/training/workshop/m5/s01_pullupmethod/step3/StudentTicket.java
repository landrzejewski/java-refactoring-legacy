package pl.training.workshop.m5.s01_pullupmethod.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: w podklasie zostaje tylko to, co naprawdę różne - cena (-25%). */
public final class StudentTicket extends Ticket {
    public StudentTicket(String title, Money basePrice) {
        super(title, basePrice);
    }

    @Override
    public Money price() {
        return basePrice().minus(basePrice().percent(25));
    }
}
