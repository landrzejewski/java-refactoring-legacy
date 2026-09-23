package pl.training.workshop.m3.s06_yagni.start;

import java.math.BigDecimal;
import java.util.Map;

/** Start: plugin "miejsce VIP +10.00". */
public final class VipRule implements PricingRule {
    @Override
    public int priority() {
        return 20;
    }

    @Override
    public boolean appliesTo(Map<String, Object> context) {
        return (int) context.get("row") >= (int) context.get("vipFromRow");
    }

    @Override
    public BigDecimal apply(Map<String, Object> context, BigDecimal price) {
        return price.add(new BigDecimal("10.00"));
    }
}
