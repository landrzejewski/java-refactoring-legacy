package pl.training.workshop.m3.s06_yagni.step1;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Map;

/** Seans poranny -5.00. */
public final class MorningRule implements PricingRule {
    @Override
    public boolean appliesTo(Map<String, Object> context) {
        return ((LocalTime) context.get("start")).getHour() < 12;
    }

    @Override
    public BigDecimal apply(Map<String, Object> context, BigDecimal price) {
        return price.subtract(new BigDecimal("5.00"));
    }
}
