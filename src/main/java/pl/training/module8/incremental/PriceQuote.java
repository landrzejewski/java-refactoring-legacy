package pl.training.module8.incremental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record PriceQuote(BigDecimal netAmount) {
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    public PriceQuote {
        Objects.requireNonNull(netAmount, "netAmount");
        if (netAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "netAmount must not be negative");
        }
        netAmount = netAmount.setScale(MONEY_SCALE, ROUNDING);
    }
}
