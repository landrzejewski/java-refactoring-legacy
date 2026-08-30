package pl.training.module4.pricing;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

final class PriceBreakdownTest {
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    @Test
    void rejectsAmountsOutsideItsMoneyContract() {
        assertAll(
                () -> assertThrows(
                        NullPointerException.class,
                        () -> breakdownWithBase(null)),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> breakdownWithBase(new BigDecimal("-0.01"))),
                () -> assertThrows(
                        ArithmeticException.class,
                        () -> breakdownWithBase(new BigDecimal("1.001"))));
    }

    private static PriceBreakdown breakdownWithBase(BigDecimal base) {
        return new PriceBreakdown(
                base,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO,
                ZERO);
    }
}
