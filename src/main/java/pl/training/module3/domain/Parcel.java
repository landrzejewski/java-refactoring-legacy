package pl.training.module3.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Parcel(BigDecimal weightKg) {
    public Parcel {
        Objects.requireNonNull(weightKg, "weightKg");

        if (weightKg.signum() <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
    }
}
