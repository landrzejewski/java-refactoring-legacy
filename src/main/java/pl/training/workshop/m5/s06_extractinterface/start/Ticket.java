package pl.training.workshop.m5.s06_extractinterface.start;

import pl.training.workshop.shared.Money;

/** Start: bilet - koszyk potrzebuje z niego tylko ceny i stawki VAT, reszta API go nie obchodzi. */
public final class Ticket {
    private final String title;
    private final String seat;
    private final Money price;

    public Ticket(String title, String seat, Money price) {
        this.title = title;
        this.seat = seat;
        this.price = price;
    }

    public String title() {
        return title;
    }

    public String seat() {
        return seat;
    }

    public Money price() {
        return price;
    }

    /** Bilety: VAT 8%. */
    public int vatPercent() {
        return 8;
    }
}
