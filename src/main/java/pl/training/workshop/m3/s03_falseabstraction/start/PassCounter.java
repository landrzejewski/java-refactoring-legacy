package pl.training.workshop.m3.s03_falseabstraction.start;

import java.math.BigDecimal;

/** Sprzedaż karnetu - format, poranek i okulary podane "na wszelki wypadek". */
public final class PassCounter {
    private final Pricing pricing = new Pricing();

    public BigDecimal pass(int entries) {
        return pricing.price("2D", entries, true, false, true);
    }
}
