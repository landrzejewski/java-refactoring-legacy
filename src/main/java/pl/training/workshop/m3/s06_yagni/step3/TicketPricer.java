package pl.training.workshop.m3.s06_yagni.step3;

import java.math.BigDecimal;

import pl.training.workshop.m3.s06_yagni.TicketQuote;

/**
 * Krok 3 (rozwiązanie): Inline Class dla obu reguł i Safe Delete interfejsu
 * PricingRule. Dwie aktualne reguły to dwa nazwane warunki - bez silnika.
 * <p>Czego YAGNI NIE zabrania i co tu zostaje: testów każdej reguły, nazwanych
 * stałych i metod (isMorning, isVip), szwu testowego (cena liczona z danych wejściowych,
 * bez zegara i statycznego stanu). Gdy pojawi się trzecia reguła z innym właścicielem
 * lub konfiguracja od biznesu, wydzielimy abstrakcję wtedy - w małych krokach, pod testami.
 */
public final class TicketPricer {
    private static final BigDecimal MORNING_DISCOUNT = new BigDecimal("5.00");
    private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");

    public BigDecimal price(TicketQuote quote) {
        BigDecimal price = basePrice(quote.format());
        if (isMorning(quote)) {
            price = price.subtract(MORNING_DISCOUNT);
        }
        if (isVip(quote)) {
            price = price.add(VIP_SURCHARGE);
        }
        return price;
    }

    private boolean isMorning(TicketQuote quote) {
        return quote.start().getHour() < 12;
    }

    private boolean isVip(TicketQuote quote) {
        return quote.row() >= quote.vipFromRow();
    }

    private BigDecimal basePrice(String format) {
        return switch (format) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
    }
}
