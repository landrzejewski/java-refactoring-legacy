package pl.training.workshop.m5.s06_extractinterface.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian. */
public final class Ticket implements Priceable {
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

    @Override
    public Money price() {
        return price;
    }

    /** Bilety: VAT 8%. */
    @Override
    public int vatPercent() {
        return 8;
    }
}
