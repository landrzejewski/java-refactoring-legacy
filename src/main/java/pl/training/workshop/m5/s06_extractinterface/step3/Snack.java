package pl.training.workshop.m5.s06_extractinterface.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: bez zmian - implementacje nie musiały nic robić, by dostać vatAmount(). */
public final class Snack implements Priceable {
    private final String name;
    private final Money price;

    public Snack(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public String name() {
        return name;
    }

    @Override
    public Money price() {
        return price;
    }

    /** Bar (popcorn, napoje): VAT 23%. */
    @Override
    public int vatPercent() {
        return 23;
    }
}
