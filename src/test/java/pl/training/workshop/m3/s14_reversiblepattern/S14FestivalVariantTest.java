package pl.training.workshop.m3.s14_reversiblepattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * Wariant festiwalowy (zmiana zachowania, więc bez start - ten jest edytowany na żywo):
 * w kroku 1 obsługuje go adapter, od kroku 2 umowy festiwalowe są odrzucane.
 */
final class S14FestivalVariantTest {
    private static final Deal FESTIVAL = new Deal("Amator", "FESTIVAL");
    private static final BigDecimal REVENUE = new BigDecimal("3000.00");

    @Test
    void step1AdapterPaysTheFestivalFeeConvertedFromCents() {
        var step1 = new pl.training.workshop.m3.s14_reversiblepattern.step1.DistributorSettlement();
        assertEquals(new BigDecimal("300.00"), step1.payout(FESTIVAL, 1, REVENUE));
        assertEquals(new BigDecimal("150.00"), step1.payout(FESTIVAL, 3, REVENUE));
    }

    @Test
    void afterTheVariantIsGoneFestivalDealsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new pl.training.workshop.m3.s14_reversiblepattern.step2
                .DistributorSettlement().payout(FESTIVAL, 1, REVENUE));
        assertThrows(IllegalArgumentException.class, () -> new pl.training.workshop.m3.s14_reversiblepattern.step3
                .DistributorSettlement().payout(FESTIVAL, 1, REVENUE));
    }
}
