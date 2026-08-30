package pl.training.module4;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

final class RentalQuoteStagesEquivalenceTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("approvedQuotes")
    void everyStageProducesApprovedQuote(
            String scenario,
            RentalRequest request,
            String expectedQuote) {
        var stage0 = new pl.training.module4.stage0.RentalQuoteService();
        var stage1 = new pl.training.module4.stage1.RentalQuoteService();
        var stage2 = new pl.training.module4.stage2.RentalQuoteService();
        var stage3 = new pl.training.module4.stage3.RentalQuoteService();

        assertAll(
                () -> assertEquals(expectedQuote, stage0.createQuote(request)),
                () -> assertEquals(expectedQuote, stage1.createQuote(request)),
                () -> assertEquals(expectedQuote, stage2.createQuote(request)),
                () -> assertEquals(expectedQuote, stage3.createQuote(request)));
    }

    private static Stream<Arguments> approvedQuotes() {
        return Stream.of(
                Arguments.of(
                        "complete generator quote",
                        new RentalRequest(
                                " Acme ",
                                EquipmentType.GENERATOR,
                                8,
                                true,
                                true),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: GENERATOR
                                Days: 8
                                Base: 960.00
                                Discount: 96.00
                                Insurance: 64.00
                                Delivery: 25.00
                                Net: 953.00
                                VAT: 219.19
                                Total: 1172.19
                                """),
                Arguments.of(
                        "insurance without delivery",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                2,
                                true,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: DRILL
                                Days: 2
                                Base: 79.98
                                Discount: 0.00
                                Insurance: 16.00
                                Delivery: 0.00
                                Net: 95.98
                                VAT: 22.08
                                Total: 118.06
                                """),
                Arguments.of(
                        "delivery without insurance",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                2,
                                false,
                                true),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: DRILL
                                Days: 2
                                Base: 79.98
                                Discount: 0.00
                                Insurance: 0.00
                                Delivery: 25.00
                                Net: 104.98
                                VAT: 24.15
                                Total: 129.13
                                """),
                Arguments.of(
                        "day before discount threshold",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.GENERATOR,
                                6,
                                false,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: GENERATOR
                                Days: 6
                                Base: 720.00
                                Discount: 0.00
                                Insurance: 0.00
                                Delivery: 0.00
                                Net: 720.00
                                VAT: 165.60
                                Total: 885.60
                                """),
                Arguments.of(
                        "discount threshold",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.GENERATOR,
                                7,
                                false,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: GENERATOR
                                Days: 7
                                Base: 840.00
                                Discount: 84.00
                                Insurance: 0.00
                                Delivery: 0.00
                                Net: 756.00
                                VAT: 173.88
                                Total: 929.88
                                """),
                Arguments.of(
                        "discount rounding",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                15,
                                false,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: DRILL
                                Days: 15
                                Base: 599.85
                                Discount: 59.99
                                Insurance: 0.00
                                Delivery: 0.00
                                Net: 539.86
                                VAT: 124.17
                                Total: 664.03
                                """),
                Arguments.of(
                        "vat rounding",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                1,
                                false,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: DRILL
                                Days: 1
                                Base: 39.99
                                Discount: 0.00
                                Insurance: 0.00
                                Delivery: 0.00
                                Net: 39.99
                                VAT: 9.20
                                Total: 49.19
                                """));
    }
}
