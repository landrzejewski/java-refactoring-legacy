package pl.training.workshop.m3.s06_yagni.start;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Start: "rozszerzalny" kontrakt reguły cenowej - priorytet, generyczny kontekst,
 * dowolne pluginy. Istnieją dokładnie dwie implementacje i nikt nie zgłosił trzeciej.
 */
public interface PricingRule {
    int priority();

    boolean appliesTo(Map<String, Object> context);

    BigDecimal apply(Map<String, Object> context, BigDecimal price);
}
