package pl.training.workshop.m3.s13_boundarycheck.step1.domain;

import java.math.BigDecimal;

/**
 * Krok 1: po Extract Class zostaje sama polityka cenowa (LCOM4 = 1)
 * i żadnego importu technologii.
 */
public final class ScreeningService {
    private final BigDecimal basePrice;
    private final BigDecimal morningDiscount;

    public ScreeningService(BigDecimal basePrice, BigDecimal morningDiscount) {
        this.basePrice = basePrice;
        this.morningDiscount = morningDiscount;
    }

    public BigDecimal price(Screening screening) {
        return isMorning(screening) ? basePrice.subtract(morningDiscount) : basePrice;
    }

    private boolean isMorning(Screening screening) {
        return screening.start().getHour() < 12;
    }
}
