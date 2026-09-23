package pl.training.workshop.m3.s06_yagni.step2;

import java.math.BigDecimal;

import pl.training.workshop.m3.s06_yagni.TicketQuote;

/** Krok 2: kontrakt na typowanych danych - bez rzutowań z Map&lt;String, Object&gt;. */
public interface PricingRule {
    boolean appliesTo(TicketQuote quote);

    BigDecimal apply(BigDecimal price);
}
