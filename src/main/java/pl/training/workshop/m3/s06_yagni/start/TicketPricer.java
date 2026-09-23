package pl.training.workshop.m3.s06_yagni.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.m3.s06_yagni.TicketQuote;

/**
 * Start: spekulatywny silnik reguł cenowych (YAGNI). Rejestr pluginów, konfiguracja
 * napisem, priorytety i kontekst Map&lt;String, Object&gt; - wszystko dla DWÓCH reguł,
 * które nie zależą od kolejności. Literówka w konfiguracji wybucha dopiero w runtime.
 */
public final class TicketPricer {
    private final RuleRegistry registry;
    private final String activeRules;

    public TicketPricer() {
        this(RuleRegistry.withDefaults(), "morning,vip");
    }

    public TicketPricer(RuleRegistry registry, String activeRules) {
        this.registry = registry;
        this.activeRules = activeRules;
    }

    public BigDecimal price(TicketQuote quote) {
        Map<String, Object> context = new HashMap<>();
        context.put("format", quote.format());
        context.put("start", quote.start());
        context.put("row", quote.row());
        context.put("vipFromRow", quote.vipFromRow());
        BigDecimal price = basePrice(quote.format());
        for (PricingRule rule : registry.resolve(activeRules)) {
            if (rule.appliesTo(context)) {
                price = rule.apply(context, price);
            }
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal basePrice(String format) {
        return switch (format) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
    }
}
