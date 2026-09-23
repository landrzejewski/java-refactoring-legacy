package pl.training.workshop.m3.s06_yagni.step1;

import java.math.BigDecimal;
import java.util.Map;

/** Miejsce VIP +10.00. */
public final class VipRule implements PricingRule {
    @Override
    public boolean appliesTo(Map<String, Object> context) {
        return (int) context.get("row") >= (int) context.get("vipFromRow");
    }

    @Override
    public BigDecimal apply(Map<String, Object> context, BigDecimal price) {
        return price.add(new BigDecimal("10.00"));
    }
}
