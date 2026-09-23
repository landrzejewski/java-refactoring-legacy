package pl.training.workshop.m6.s04_encapsulatefactory.step3.tickets;

import pl.training.workshop.shared.Money;

/** Krok 3: klasa pakietowa, miejsce VIP: +10.00. */
record VipTicket(String title, Money base, int row) implements Ticket {
    @Override
    public Money price() {
        return base.plus(Money.of("10.00"));
    }

    @Override
    public String describe() {
        return title + " r" + row + " VIP " + price();
    }
}
