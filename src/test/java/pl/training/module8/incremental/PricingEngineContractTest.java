package pl.training.module8.incremental;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class PricingEngineContractTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("engines")
    void everyProductionImplementationCalculatesCanonicalExamples(
            String description,
            PricingEngine engine) {
        assertAll(
                () -> assertQuote(engine, "10.00", 3, "0", "30.00"),
                () -> assertQuote(engine, "10.00", 3, "1", "0.00"),
                () -> assertQuote(engine, "0.01", 1, "0.5", "0.01"),
                () -> assertQuote(
                        engine, "19.995", 2, "0.12555", "34.98"),
                () -> assertQuote(
                        engine, "0.05", 3, "0.3333", "0.10"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("engines")
    void everyProductionImplementationIsDeterministicAndReturnsMoney(
            String description,
            PricingEngine engine) {
        var request = new PriceRequest(
                new BigDecimal("17.49"),
                7,
                new BigDecimal("0.075"));

        PriceQuote first = engine.quote(request);
        PriceQuote second = engine.quote(request);

        assertAll(
                () -> assertEquals(first, second),
                () -> assertEquals(2, first.netAmount().scale()),
                () -> assertEquals(new BigDecimal("113.25"),
                        first.netAmount()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("engines")
    void everyProductionImplementationRejectsMissingRequest(
            String description,
            PricingEngine engine) {
        NullPointerException failure = assertThrows(
                NullPointerException.class,
                () -> engine.quote(null));

        assertEquals("request", failure.getMessage());
    }

    private static void assertQuote(
            PricingEngine engine,
            String unitPrice,
            int quantity,
            String discountRate,
            String expected) {
        PriceQuote quote = engine.quote(new PriceRequest(
                new BigDecimal(unitPrice),
                quantity,
                new BigDecimal(discountRate)));

        assertEquals(new BigDecimal(expected), quote.netAmount());
        assertEquals(2, quote.netAmount().scale());
    }

    private static Stream<Arguments> engines() {
        return Stream.of(
                Arguments.of(
                        "legacy implementation behind an adapter",
                        new LegacyPricingEngineAdapter()),
                Arguments.of(
                        "candidate implementation",
                        new CandidatePricingEngine()));
    }
}
