package pl.training.module1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

final class RiskClassifierTest {
    @Test
    void countsIndependentRiskConditions() {
        OrderSummary order = new OrderSummary(
                new BigDecimal("1500.00"),
                true,
                List.of(new Item(true), new Item(false)));

        assertEquals(3, RiskClassifier.riskLevel(order));
    }
}
