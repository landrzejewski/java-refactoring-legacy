package pl.training.module8.incremental;

import java.util.Objects;

public final class LegacyPricingEngineAdapter implements PricingEngine {
    private final LegacyPriceCalculator calculator;

    public LegacyPricingEngineAdapter() {
        this(new LegacyPriceCalculator());
    }

    public LegacyPricingEngineAdapter(LegacyPriceCalculator calculator) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
    }

    @Override
    public PriceQuote quote(PriceRequest request) {
        Objects.requireNonNull(request, "request");
        return new PriceQuote(calculator.calculate(
                request.unitPrice(),
                request.quantity(),
                request.discountRate().movePointRight(2)));
    }
}
