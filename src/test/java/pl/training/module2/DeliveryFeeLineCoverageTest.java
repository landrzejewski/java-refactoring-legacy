package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class DeliveryFeeLineCoverageTest {
    @Test
    void premiumCustomerHasFreeDelivery() {
        assertEquals(0, DeliveryFee.fee(true));
    }
}
