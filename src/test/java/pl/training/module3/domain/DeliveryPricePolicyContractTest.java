package pl.training.module3.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class DeliveryPricePolicyContractTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("policies")
    void everyPolicyObeysTheSubstitutionContract(
            String description,
            ShippingMethod expectedMethod,
            DeliveryPricePolicy policy) {
        Parcel parcel = new Parcel(new BigDecimal("100.00"));

        ShippingMethod firstMethod = policy.method();
        ShippingMethod secondMethod = policy.method();
        BigDecimal firstResult = policy.priceFor(parcel);
        BigDecimal secondResult = policy.priceFor(parcel);

        assertAll(
                () -> assertEquals(expectedMethod, firstMethod),
                () -> assertEquals(firstMethod, secondMethod),
                () -> assertTrue(firstResult.signum() >= 0),
                () -> assertEquals(2, firstResult.scale()),
                () -> assertEquals(firstResult, secondResult));
    }

    private static Stream<Arguments> policies() {
        FuelSurcharge surcharge = new FuelSurcharge(new BigDecimal("0.08"));
        return Stream.of(
                Arguments.of(
                        "standard policy",
                        ShippingMethod.STANDARD,
                        new StandardDeliveryPricePolicy(surcharge)),
                Arguments.of(
                        "express policy",
                        ShippingMethod.EXPRESS,
                        new ExpressDeliveryPricePolicy(surcharge)));
    }
}
