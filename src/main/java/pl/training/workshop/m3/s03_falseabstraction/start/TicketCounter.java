package pl.training.workshop.m3.s03_falseabstraction.start;

import java.math.BigDecimal;

/** Sprzedaż pojedynczego biletu - "quantity" i "pass" nic tu nie znaczą. */
public final class TicketCounter {
    private final Pricing pricing = new Pricing();

    public BigDecimal ticket(String format, boolean morning, boolean ownGlasses) {
        return pricing.price(format, 1, false, morning, ownGlasses);
    }
}
