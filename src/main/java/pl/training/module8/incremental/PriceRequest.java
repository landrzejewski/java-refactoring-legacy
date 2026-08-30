package pl.training.module8.incremental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record PriceRequest(
        BigDecimal unitPrice,
        int quantity,
        BigDecimal discountRate) {
    private static final int MONEY_SCALE = 2;
    private static final int RATE_SCALE = 4;
    private static final BigDecimal MAXIMUM_DISCOUNT_RATE = BigDecimal.ONE;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public PriceRequest {
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(discountRate, "discountRate");
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException(
                    "unitPrice must not be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (discountRate.signum() < 0
                || discountRate.compareTo(MAXIMUM_DISCOUNT_RATE) > 0) {
            throw new IllegalArgumentException(
                    "discountRate must be between 0 and 1");
        }

        unitPrice = unitPrice.setScale(MONEY_SCALE, ROUNDING);
        discountRate = discountRate.setScale(RATE_SCALE, ROUNDING);
    }
}
