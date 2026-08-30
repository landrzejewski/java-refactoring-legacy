package pl.training.module1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class DiscountPolicyTest {
    @Test
    void combinesThresholdAndVipDiscount() {
        assertEquals(15, DiscountPolicy.discountPercent(100, true));
    }
}
