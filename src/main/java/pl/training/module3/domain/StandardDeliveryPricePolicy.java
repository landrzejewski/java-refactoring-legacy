package pl.training.module3.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class StandardDeliveryPricePolicy implements DeliveryPricePolicy {
    private static final BigDecimal BASE_PRICE = new BigDecimal("10.00");
    private static final BigDecimal PRICE_PER_KG = new BigDecimal("2.00");

    private final FuelSurcharge fuelSurcharge;

    public StandardDeliveryPricePolicy(FuelSurcharge fuelSurcharge) {
        this.fuelSurcharge = Objects.requireNonNull(fuelSurcharge);
    }

    @Override
    public ShippingMethod method() {
        return ShippingMethod.STANDARD;
    }

    @Override
    public BigDecimal priceFor(Parcel parcel) {
        BigDecimal baseAmount = BASE_PRICE.add(
                parcel.weightKg().multiply(PRICE_PER_KG));
        return fuelSurcharge.addTo(baseAmount);
    }
}
