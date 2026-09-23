package pl.training.workshop.m3.s06_yagni.step2;

import java.math.BigDecimal;

import pl.training.workshop.m3.s06_yagni.TicketQuote;

/** Miejsce VIP +10.00. */
public final class VipRule implements PricingRule {
    @Override
    public boolean appliesTo(TicketQuote quote) {
        return quote.row() >= quote.vipFromRow();
    }

    @Override
    public BigDecimal apply(BigDecimal price) {
        return price.add(new BigDecimal("10.00"));
    }
}
