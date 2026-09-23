package pl.training.workshop.m8.s10_qualitygate.sample.clean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Test próbki czystej: każda publiczna metoda ma przypadek. */
final class PriceTableTest {
    @Test
    void basePriceDependsOnFormat() {
        assertEquals(32, new PriceTable().basePrice("3D"));
    }

    @Test
    void vipSurchargeFromRowTen() {
        assertEquals(0, new PriceTable().vipSurcharge(9));
        assertEquals(10, new PriceTable().vipSurcharge(10));
    }
}
