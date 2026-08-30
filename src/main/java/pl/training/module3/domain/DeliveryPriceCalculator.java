package pl.training.module3.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class DeliveryPriceCalculator {
    private final Map<ShippingMethod, DeliveryPricePolicy> policies;

    public DeliveryPriceCalculator(
            Collection<? extends DeliveryPricePolicy> policies) {
        Objects.requireNonNull(policies, "policies");

        if (policies.isEmpty()) {
            throw new IllegalArgumentException("At least one policy is required");
        }

        EnumMap<ShippingMethod, DeliveryPricePolicy> indexedPolicies =
                new EnumMap<>(ShippingMethod.class);
        for (DeliveryPricePolicy policy : policies) {
            Objects.requireNonNull(policy, "policy");
            ShippingMethod method = Objects.requireNonNull(
                    policy.method(),
                    "policy.method()");
            DeliveryPricePolicy previous = indexedPolicies.putIfAbsent(
                    method,
                    policy);

            if (previous != null) {
                throw new IllegalArgumentException(
                        "Duplicate policy for method: " + method);
            }
        }
        this.policies = Map.copyOf(indexedPolicies);
    }

    public BigDecimal priceFor(ShippingMethod method, Parcel parcel) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(parcel, "parcel");

        DeliveryPricePolicy policy = policies.get(method);
        if (policy == null) {
            throw new IllegalArgumentException(
                    "No pricing policy for method: " + method);
        }
        return policy.priceFor(parcel);
    }
}
