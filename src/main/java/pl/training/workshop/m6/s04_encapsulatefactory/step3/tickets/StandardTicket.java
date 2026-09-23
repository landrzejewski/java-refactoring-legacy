package pl.training.workshop.m6.s04_encapsulatefactory.step3.tickets;

import pl.training.workshop.shared.Money;

/** Krok 3: klasa pakietowa - poza pakietem tickets nie da się jej utworzyć ani zaimportować. */
record StandardTicket(String title, Money base, int row) implements Ticket {
    @Override
    public Money price() {
        return base;
    }

    @Override
    public String describe() {
        return title + " r" + row + " " + price();
    }
}
