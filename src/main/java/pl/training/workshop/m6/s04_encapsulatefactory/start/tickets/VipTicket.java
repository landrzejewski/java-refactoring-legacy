package pl.training.workshop.m6.s04_encapsulatefactory.start.tickets;

import pl.training.workshop.shared.Money;

/** Bilet na miejsce VIP: +10.00. Publiczna klasa z publicznym konstruktorem. */
public final class VipTicket implements Ticket {
    private final String title;
    private final Money base;
    private final int row;

    public VipTicket(String title, Money base, int row) {
        this.title = title;
        this.base = base;
        this.row = row;
    }

    @Override
    public Money price() {
        return base.plus(Money.of("10.00"));
    }

    @Override
    public String describe() {
        return title + " r" + row + " VIP " + price();
    }
}
