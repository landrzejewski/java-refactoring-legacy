package pl.training.workshop.m3.s06_yagni.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import pl.training.workshop.m3.s06_yagni.TicketQuote;

/**
 * Krok 2: Change Signature - reguły dostają {@link TicketQuote} zamiast generycznej
 * mapy "kontekstu". Znika budowanie mapy i rzutowania; kompilator pilnuje nazw pól.
 */
public final class TicketPricer {
    private final List<PricingRule> rules = List.of(new MorningRule(), new VipRule());

    public BigDecimal price(TicketQuote quote) {
        BigDecimal price = basePrice(quote.format());
        for (PricingRule rule : rules) {
            if (rule.appliesTo(quote)) {
                price = rule.apply(price);
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
