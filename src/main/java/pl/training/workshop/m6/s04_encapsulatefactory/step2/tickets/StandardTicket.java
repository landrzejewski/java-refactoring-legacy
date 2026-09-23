package pl.training.workshop.m6.s04_encapsulatefactory.step2.tickets;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian. Bilet na zwykłe miejsce. Publiczny konstruktor - klienci robią new. */
public final class StandardTicket implements Ticket {
    private final String title;
    private final Money base;
    private final int row;

    public StandardTicket(String title, Money base, int row) {
        this.title = title;
        this.base = base;
        this.row = row;
    }

    @Override
    public Money price() {
        return base;
    }

    @Override
    public String describe() {
        return title + " r" + row + " " + price();
    }
}
