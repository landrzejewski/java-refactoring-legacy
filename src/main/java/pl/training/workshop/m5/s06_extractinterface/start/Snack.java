package pl.training.workshop.m5.s06_extractinterface.start;

import pl.training.workshop.shared.Money;

/** Start: przekąska z baru - te same dwie operacje co w Ticket, ale bez wspólnego typu. */
public final class Snack {
    private final String name;
    private final Money price;

    public Snack(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public String name() {
        return name;
    }

    public Money price() {
        return price;
    }

    /** Bar (popcorn, napoje): VAT 23%. */
    public int vatPercent() {
        return 23;
    }
}
