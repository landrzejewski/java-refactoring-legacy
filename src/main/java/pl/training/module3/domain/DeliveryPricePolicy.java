package pl.training.module3.domain;

import java.math.BigDecimal;

public interface DeliveryPricePolicy {
    /**
     * Returns the stable, non-null shipping method handled by this policy.
     */
    ShippingMethod method();

    /**
     * Returns a deterministic, non-negative amount with scale two for every
     * valid parcel, without changing the parcel or producing side effects.
     */
    BigDecimal priceFor(Parcel parcel);
}
