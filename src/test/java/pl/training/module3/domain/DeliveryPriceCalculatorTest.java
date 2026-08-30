package pl.training.module3.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

final class DeliveryPriceCalculatorTest {
    @ParameterizedTest
    @MethodSource("deliveryPrices")
    void delegatesToPolicySelectedByShippingMethod(
            ShippingMethod method,
            BigDecimal expectedPrice) {
        DeliveryPriceCalculator calculator = calculatorWithAllPolicies();

        BigDecimal price = calculator.priceFor(
                method,
                new Parcel(new BigDecimal("3.00")));

        assertEquals(expectedPrice, price);
    }

    @Test
    void rejectsDuplicatePolicyForOneShippingMethod() {
        FuelSurcharge surcharge = fuelSurcharge();

        assertThrows(
                IllegalArgumentException.class,
                () -> new DeliveryPriceCalculator(List.of(
                        new StandardDeliveryPricePolicy(surcharge),
                        new StandardDeliveryPricePolicy(surcharge))));
    }

    @Test
    void reportsMissingPolicy() {
        DeliveryPriceCalculator calculator = new DeliveryPriceCalculator(
                List.of(new StandardDeliveryPricePolicy(fuelSurcharge())));

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.priceFor(
                        ShippingMethod.EXPRESS,
                        new Parcel(new BigDecimal("3.00"))));
    }

    private static Stream<Arguments> deliveryPrices() {
        return Stream.of(
                Arguments.of(
                        ShippingMethod.STANDARD,
                        new BigDecimal("17.28")),
                Arguments.of(
                        ShippingMethod.EXPRESS,
                        new BigDecimal("31.32")));
    }

    private static DeliveryPriceCalculator calculatorWithAllPolicies() {
        FuelSurcharge surcharge = fuelSurcharge();
        return new DeliveryPriceCalculator(List.of(
                new StandardDeliveryPricePolicy(surcharge),
                new ExpressDeliveryPricePolicy(surcharge)));
    }

    private static FuelSurcharge fuelSurcharge() {
        return new FuelSurcharge(new BigDecimal("0.08"));
    }
}
