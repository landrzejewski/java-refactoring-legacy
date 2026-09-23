package pl.training.workshop.m3.s06_yagni.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.training.workshop.m3.s06_yagni.TicketQuote;

/**
 * Krok 1: Inline Class - rejestr pluginów i konfiguracja napisem znikają.
 * Reguły to zwykła lista tworzona przez new; priorytet (Safe Delete) był potrzebny
 * tylko do sortowania w rejestrze. Literówka w nazwie reguły już się nie skompiluje.
 */
public final class TicketPricer {
    private final List<PricingRule> rules = List.of(new MorningRule(), new VipRule());

    public BigDecimal price(TicketQuote quote) {
        Map<String, Object> context = new HashMap<>();
        context.put("format", quote.format());
        context.put("start", quote.start());
        context.put("row", quote.row());
        context.put("vipFromRow", quote.vipFromRow());
        BigDecimal price = basePrice(quote.format());
        for (PricingRule rule : rules) {
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
