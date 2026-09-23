package pl.training.workshop.m8.s10_qualitygate.sample.broken;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import pl.training.workshop.m8.s10_qualitygate.sample.clean.PriceTable;

/**
 * Fikstura dla bramki: "test", który nie przechodzi. Celowo NIE używa JUnit (własna adnotacja
 * Test), więc Maven i IDE go nie uruchamiają - uruchamia go tylko QualityGate z kroku 4.
 */
final class PriceTableTest {
    @Retention(RetentionPolicy.RUNTIME)
    @interface Test {
    }

    @Test
    void basePriceOfImax() {
        check(new PriceTable().basePrice("IMAX") == 40);
    }

    @Test
    void vipSurchargeStartsAtRowNine() {
        check(new PriceTable().vipSurcharge(9) == 10);
    }

    private static void check(boolean condition) {
        if (!condition) {
            throw new AssertionError("oczekiwanie niespełnione");
        }
    }
}
