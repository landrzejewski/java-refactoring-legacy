package pl.training.workshop.m2.s01_yagni.step1;

import java.math.BigDecimal;
import java.util.Map;

/** Krok 1: bez priorytetu - kolejność wyznacza lista w TicketPricer. */
public interface PricingRule {
    boolean appliesTo(Map<String, Object> context);

    BigDecimal apply(Map<String, Object> context, BigDecimal price);
}
