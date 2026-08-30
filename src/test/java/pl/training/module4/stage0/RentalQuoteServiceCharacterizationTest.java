package pl.training.module4.stage0;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

final class RentalQuoteServiceCharacterizationTest {
    private final RentalQuoteService service = new RentalQuoteService();

    @Test
    void documentsCompleteGeneratorQuote() {
        RentalRequest request = new RentalRequest(
                " Acme ",
                EquipmentType.GENERATOR,
                8,
                true,
                true);

        String quote = service.createQuote(request);

        assertEquals("""
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
                """, quote);
    }

    @Test
    void documentsDiscountBoundary() {
        String sixDays = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.GENERATOR,
                6,
                false,
                false));
        String sevenDays = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.GENERATOR,
                7,
                false,
                false));

        assertTrue(sixDays.contains("Discount: 0.00\n"));
        assertTrue(sixDays.contains("Total: 885.60\n"));
        assertTrue(sevenDays.contains("Discount: 84.00\n"));
        assertTrue(sevenDays.contains("Total: 929.88\n"));
    }

    @Test
    void documentsVatRounding() {
        String quote = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                1,
                false,
                false));

        assertTrue(quote.contains("VAT: 9.20\n"));
        assertTrue(quote.contains("Total: 49.19\n"));
    }

    @Test
    void distinguishesInsuranceFromDelivery() {
        String insuranceOnly = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                2,
                true,
                false));
        String deliveryOnly = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                2,
                false,
                true));

        assertAll(
                () -> assertTrue(
                        insuranceOnly.contains("Insurance: 16.00\n")),
                () -> assertTrue(
                        insuranceOnly.contains("Delivery: 0.00\n")),
                () -> assertTrue(
                        insuranceOnly.contains("Total: 118.06\n")),
                () -> assertTrue(
                        deliveryOnly.contains("Insurance: 0.00\n")),
                () -> assertTrue(
                        deliveryOnly.contains("Delivery: 25.00\n")),
                () -> assertTrue(
                        deliveryOnly.contains("Total: 129.13\n")));
    }

    @Test
    void documentsDiscountRounding() {
        String quote = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                15,
                false,
                false));

        assertTrue(quote.contains("Discount: 59.99\n"));
        assertTrue(quote.contains("Net: 539.86\n"));
        assertTrue(quote.contains("Total: 664.03\n"));
    }
}
