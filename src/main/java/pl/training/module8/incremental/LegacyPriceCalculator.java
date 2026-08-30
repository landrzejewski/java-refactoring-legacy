package pl.training.module8.incremental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class LegacyPriceCalculator {
    private static final int MONEY_SCALE = 2;
    private static final int PERCENT_SCALE = 2;
    private static final BigDecimal MAXIMUM_DISCOUNT_PERCENT =
            new BigDecimal("100");
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public BigDecimal calculate(
            BigDecimal unitPrice,
            int quantity,
            BigDecimal discountPercent) {
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(discountPercent, "discountPercent");
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException(
                    "unitPrice must not be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (discountPercent.signum() < 0
                || discountPercent.compareTo(
                        MAXIMUM_DISCOUNT_PERCENT) > 0) {
            throw new IllegalArgumentException(
                    "discountPercent must be between 0 and 100");
        }

        BigDecimal normalizedUnitPrice = unitPrice.setScale(
                MONEY_SCALE, ROUNDING);
        BigDecimal normalizedDiscountPercent = discountPercent.setScale(
                PERCENT_SCALE, ROUNDING);
        BigDecimal grossAmount = normalizedUnitPrice.multiply(
                BigDecimal.valueOf(quantity));
        BigDecimal discountAmount = grossAmount
                .multiply(normalizedDiscountPercent)
                .movePointLeft(2)
                .setScale(MONEY_SCALE, ROUNDING);

        return grossAmount.subtract(discountAmount)
                .setScale(MONEY_SCALE, ROUNDING);
    }
}
