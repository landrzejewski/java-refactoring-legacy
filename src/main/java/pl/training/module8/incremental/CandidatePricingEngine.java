package pl.training.module8.incremental;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class CandidatePricingEngine implements PricingEngine {
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    @Override
    public PriceQuote quote(PriceRequest request) {
        Objects.requireNonNull(request, "request");

        BigDecimal grossAmount = request.unitPrice().multiply(
                BigDecimal.valueOf(request.quantity()));
        BigDecimal discountAmount = grossAmount.multiply(
                request.discountRate()).setScale(MONEY_SCALE, ROUNDING);

        return new PriceQuote(grossAmount.subtract(discountAmount));
    }
}
