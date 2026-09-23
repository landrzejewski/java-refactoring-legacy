package pl.training.workshop.m3.s06_yagni.step2;

import java.math.BigDecimal;

import pl.training.workshop.m3.s06_yagni.TicketQuote;

/** Seans poranny -5.00. */
public final class MorningRule implements PricingRule {
    @Override
    public boolean appliesTo(TicketQuote quote) {
        return quote.start().getHour() < 12;
    }

    @Override
    public BigDecimal apply(BigDecimal price) {
        return price.subtract(new BigDecimal("5.00"));
    }
}
