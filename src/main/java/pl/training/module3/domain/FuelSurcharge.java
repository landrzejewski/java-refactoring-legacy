package pl.training.module3.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class FuelSurcharge {
    private final BigDecimal rate;

    public FuelSurcharge(BigDecimal rate) {
        this.rate = Objects.requireNonNull(rate, "rate");

        if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    "Fuel surcharge rate must be between zero and one");
        }
    }

    public BigDecimal addTo(BigDecimal baseAmount) {
        Objects.requireNonNull(baseAmount, "baseAmount");

        if (baseAmount.signum() < 0) {
            throw new IllegalArgumentException("Base amount must not be negative");
        }

        return baseAmount
                .add(baseAmount.multiply(rate))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
