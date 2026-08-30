package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.module6.strategy.after.DeploymentCostCalculator;
import pl.training.module6.strategy.after.ExpeditedCostPolicy;
import pl.training.module6.strategy.after.StandardCostPolicy;
import pl.training.module6.strategy.before.LegacyDeploymentCostCalculator;
import pl.training.module6.strategy.before.LegacyDeploymentCostCalculator.DeploymentMode;

final class StrategyEquivalenceTest {
    @Test
    void preservesEveryLegacyCalculationVariant() {
        var legacy = new LegacyDeploymentCostCalculator();

        for (long baseCost : new long[] {0, 1, 4, 10_001}) {
            assertEquals(
                    legacy.calculate(baseCost, DeploymentMode.STANDARD),
                    new DeploymentCostCalculator(new StandardCostPolicy())
                            .calculate(baseCost));
            assertEquals(
                    legacy.calculate(baseCost, DeploymentMode.EXPEDITED),
                    new DeploymentCostCalculator(new ExpeditedCostPolicy())
                            .calculate(baseCost));
        }
    }

    @Test
    void keepsInputValidationInTheContext() {
        var calculator = new DeploymentCostCalculator(ignored -> 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(-1));

        assertEquals("base cost must not be negative", exception.getMessage());
    }

    @Test
    void preservesOverflowPolicyOfTheExpeditedVariant() {
        var legacy = new LegacyDeploymentCostCalculator();
        var refactored = new DeploymentCostCalculator(new ExpeditedCostPolicy());

        assertThrows(
                ArithmeticException.class,
                () -> legacy.calculate(Long.MAX_VALUE, DeploymentMode.EXPEDITED));
        assertThrows(
                ArithmeticException.class,
                () -> refactored.calculate(Long.MAX_VALUE));
    }
}
