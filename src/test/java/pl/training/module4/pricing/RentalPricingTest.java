package pl.training.module4.pricing;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

final class RentalPricingTest {
    @Test
    void calculatesApprovedPriceBreakdown() {
        RentalRequest request = new RentalRequest(
                "Acme",
                EquipmentType.GENERATOR,
                8,
                true,
                true);

        PriceBreakdown price = RentalPricing.standard().calculate(request);

        assertEquals(new PriceBreakdown(
                new BigDecimal("960.00"),
                new BigDecimal("96.00"),
                new BigDecimal("64.00"),
                new BigDecimal("25.00"),
                new BigDecimal("953.00"),
                new BigDecimal("219.19"),
                new BigDecimal("1172.19")), price);
    }

    @Test
    void rejectsIncompleteRateConfiguration() {
        Map<EquipmentType, BigDecimal> incompleteRates = Map.of(
                EquipmentType.DRILL,
                new BigDecimal("39.99"));

        assertThrows(
                IllegalArgumentException.class,
                () -> new RentalPricing(
                        incompleteRates,
                        new BigDecimal("0.10")));
    }

    @Test
    void rejectsNonPositiveDailyRate() {
        EnumMap<EquipmentType, BigDecimal> rates = completeRates();
        rates.put(EquipmentType.DRILL, BigDecimal.ZERO);

        assertThrows(
                IllegalArgumentException.class,
                () -> new RentalPricing(rates, new BigDecimal("0.10")));
    }

    @Test
    void rejectsNegativeDailyRate() {
        EnumMap<EquipmentType, BigDecimal> rates = completeRates();
        rates.put(EquipmentType.DRILL, new BigDecimal("-1.00"));

        assertThrows(
                IllegalArgumentException.class,
                () -> new RentalPricing(rates, new BigDecimal("0.10")));
    }

    @Test
    void rejectsDailyRateThatRoundsToZero() {
        EnumMap<EquipmentType, BigDecimal> rates = completeRates();
        rates.put(EquipmentType.DRILL, new BigDecimal("0.004"));

        assertThrows(
                IllegalArgumentException.class,
                () -> new RentalPricing(rates, new BigDecimal("0.10")));
    }

    @Test
    void rejectsDiscountOutsideClosedUnitInterval() {
        EnumMap<EquipmentType, BigDecimal> rates = completeRates();

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new RentalPricing(
                                rates,
                                new BigDecimal("-0.01"))),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new RentalPricing(
                                rates,
                                new BigDecimal("1.01"))));
    }

    @Test
    void ownsDefensiveCopyOfDailyRates() {
        EnumMap<EquipmentType, BigDecimal> rates = completeRates();
        RentalPricing pricing =
                new RentalPricing(rates, new BigDecimal("0.10"));

        rates.put(EquipmentType.DRILL, new BigDecimal("1.00"));
        PriceBreakdown price = pricing.calculate(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                1,
                false,
                false));

        assertEquals(new BigDecimal("39.99"), price.baseRentalCost());
    }

    private static EnumMap<EquipmentType, BigDecimal> completeRates() {
        EnumMap<EquipmentType, BigDecimal> rates =
                new EnumMap<>(EquipmentType.class);
        rates.put(EquipmentType.DRILL, new BigDecimal("39.99"));
        rates.put(EquipmentType.GENERATOR, new BigDecimal("120.00"));
        return rates;
    }
}
