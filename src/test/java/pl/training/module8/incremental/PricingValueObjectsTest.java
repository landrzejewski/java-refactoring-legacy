package pl.training.module8.incremental;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

final class PricingValueObjectsTest {
    @Test
    void requestNormalizesMoneyAndRateUsingTheDeclaredPolicy() {
        var request = new PriceRequest(
                new BigDecimal("12.345"),
                2,
                new BigDecimal("0.12345"));

        assertAll(
                () -> assertEquals(
                        new BigDecimal("12.34"), request.unitPrice()),
                () -> assertEquals(
                        new BigDecimal("0.1234"), request.discountRate()),
                () -> assertEquals(2, request.unitPrice().scale()),
                () -> assertEquals(4, request.discountRate().scale()));
    }

    @Test
    void requestRejectsInvalidValuesBeforeRounding() {
        assertFailure(
                NullPointerException.class,
                "unitPrice",
                () -> new PriceRequest(null, 1, BigDecimal.ZERO));
        assertFailure(
                NullPointerException.class,
                "discountRate",
                () -> new PriceRequest(BigDecimal.ONE, 1, null));
        assertFailure(
                IllegalArgumentException.class,
                "unitPrice must not be negative",
                () -> new PriceRequest(
                        new BigDecimal("-0.001"), 1, BigDecimal.ZERO));
        assertFailure(
                IllegalArgumentException.class,
                "quantity must be positive",
                () -> new PriceRequest(BigDecimal.ONE, 0, BigDecimal.ZERO));
        assertFailure(
                IllegalArgumentException.class,
                "discountRate must be between 0 and 1",
                () -> new PriceRequest(
                        BigDecimal.ONE, 1, new BigDecimal("-0.00001")));
        assertFailure(
                IllegalArgumentException.class,
                "discountRate must be between 0 and 1",
                () -> new PriceRequest(
                        BigDecimal.ONE, 1, new BigDecimal("1.00001")));
    }

    @Test
    void quoteNormalizesMoneyAndRejectsInvalidAmounts() {
        assertEquals(
                new BigDecimal("10.12"),
                new PriceQuote(new BigDecimal("10.125")).netAmount());
        assertFailure(
                NullPointerException.class,
                "netAmount",
                () -> new PriceQuote(null));
        assertFailure(
                IllegalArgumentException.class,
                "netAmount must not be negative",
                () -> new PriceQuote(new BigDecimal("-0.001")));
    }

    private static void assertFailure(
            Class<? extends RuntimeException> expectedType,
            String expectedMessage,
            Runnable action) {
        RuntimeException failure = assertThrows(expectedType, action::run);
        assertEquals(expectedMessage, failure.getMessage());
    }
}
