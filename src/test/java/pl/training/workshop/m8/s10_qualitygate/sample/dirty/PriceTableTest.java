package pl.training.workshop.m8.s10_qualitygate.sample.dirty;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Test próbki "brudnej": zielony, ale sprawdza tylko basePrice (reszta bez pokrycia). */
final class PriceTableTest {
    @Test
    void basePriceDependsOnFormat() {
        assertEquals(40, new PriceTable().basePrice("IMAX"));
        assertEquals(25, new PriceTable().basePrice("2D"));
    }
}
