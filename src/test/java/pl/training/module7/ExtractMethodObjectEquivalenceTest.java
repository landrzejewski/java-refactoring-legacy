package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module7.methodobject.DeploymentRiskInput;
import pl.training.module7.methodobject.RiskAssessment;
import pl.training.module7.methodobject.RiskLevel;
import pl.training.module7.methodobject.after.DeploymentRiskCalculator;
import pl.training.module7.methodobject.before.LegacyDeploymentRiskCalculator;

final class ExtractMethodObjectEquivalenceTest {
    @Test
    void preservesRiskAssessmentsAcrossRepresentativeInputs() {
        var before = new LegacyDeploymentRiskCalculator();
        var after = new DeploymentRiskCalculator();
        List<DeploymentRiskInput> inputs = List.of(
                new DeploymentRiskInput(0, 0, 0, false),
                new DeploymentRiskInput(5, 0, 0, true),
                new DeploymentRiskInput(10, 2, 1, true),
                new DeploymentRiskInput(29, 0, 0, false),
                new DeploymentRiskInput(30, 0, 0, false),
                new DeploymentRiskInput(69, 0, 0, false),
                new DeploymentRiskInput(70, 0, 0, false),
                new DeploymentRiskInput(Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        false));

        for (DeploymentRiskInput input : inputs) {
            assertEquals(before.calculate(input), after.calculate(input),
                    "Different assessment for " + input);
        }
    }

    @Test
    void preservesScoreClampingAndClassificationBoundaries() {
        var calculator = new DeploymentRiskCalculator();

        assertEquals(new RiskAssessment(0, RiskLevel.LOW),
                calculator.calculate(
                        new DeploymentRiskInput(5, 0, 0, true)));
        assertEquals(new RiskAssessment(29, RiskLevel.LOW),
                calculator.calculate(
                        new DeploymentRiskInput(29, 0, 0, false)));
        assertEquals(new RiskAssessment(30, RiskLevel.MEDIUM),
                calculator.calculate(
                        new DeploymentRiskInput(30, 0, 0, false)));
        assertEquals(new RiskAssessment(69, RiskLevel.MEDIUM),
                calculator.calculate(
                        new DeploymentRiskInput(69, 0, 0, false)));
        assertEquals(new RiskAssessment(70, RiskLevel.HIGH),
                calculator.calculate(
                        new DeploymentRiskInput(70, 0, 0, false)));
        assertEquals(new RiskAssessment(100, RiskLevel.HIGH),
                calculator.calculate(new DeploymentRiskInput(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        false)));
    }

    @Test
    void preservesEachRiskWeightAndRollbackReduction() {
        var calculator = new DeploymentRiskCalculator();

        assertEquals(new RiskAssessment(20, RiskLevel.LOW),
                calculator.calculate(
                        new DeploymentRiskInput(0, 1, 0, false)));
        assertEquals(new RiskAssessment(10, RiskLevel.LOW),
                calculator.calculate(
                        new DeploymentRiskInput(0, 0, 1, false)));
        assertEquals(new RiskAssessment(35, RiskLevel.MEDIUM),
                calculator.calculate(
                        new DeploymentRiskInput(50, 0, 0, true)));
    }

    @Test
    void facadeCreatesAnIndependentCalculationForEveryInvocation() {
        var calculator = new DeploymentRiskCalculator();
        var low = new DeploymentRiskInput(10, 0, 0, false);
        var high = new DeploymentRiskInput(80, 0, 0, false);

        assertEquals(new RiskAssessment(10, RiskLevel.LOW),
                calculator.calculate(low));
        assertEquals(new RiskAssessment(80, RiskLevel.HIGH),
                calculator.calculate(high));
        assertEquals(new RiskAssessment(10, RiskLevel.LOW),
                calculator.calculate(low));
    }

    @Test
    void preservesMissingInputFailure() {
        var before = new LegacyDeploymentRiskCalculator();
        var after = new DeploymentRiskCalculator();

        NullPointerException beforeFailure = assertThrows(
                NullPointerException.class, () -> before.calculate(null));
        NullPointerException afterFailure = assertThrows(
                NullPointerException.class, () -> after.calculate(null));

        assertEquals(beforeFailure.getMessage(), afterFailure.getMessage());
    }

    @Test
    void sharedInputModelRejectsNegativeCounts() {
        assertInvalidInput(
                () -> new DeploymentRiskInput(-1, 0, 0, false),
                "changedFiles must not be negative");
        assertInvalidInput(
                () -> new DeploymentRiskInput(0, -1, 0, false),
                "criticalServices must not be negative");
        assertInvalidInput(
                () -> new DeploymentRiskInput(0, 0, -1, false),
                "failedChecks must not be negative");
    }

    @Test
    void assessmentProtectsItsRangeAndRequiredLevel() {
        IllegalArgumentException belowRange = assertThrows(
                IllegalArgumentException.class,
                () -> new RiskAssessment(-1, RiskLevel.LOW));
        IllegalArgumentException aboveRange = assertThrows(
                IllegalArgumentException.class,
                () -> new RiskAssessment(101, RiskLevel.HIGH));
        NullPointerException missingLevel = assertThrows(
                NullPointerException.class,
                () -> new RiskAssessment(10, null));

        assertEquals("score must be between 0 and 100",
                belowRange.getMessage());
        assertEquals("score must be between 0 and 100",
                aboveRange.getMessage());
        assertEquals("level", missingLevel.getMessage());
    }

    private static void assertInvalidInput(
            Runnable constructor,
            String expectedMessage) {
        IllegalArgumentException failure = assertThrows(
                IllegalArgumentException.class, constructor::run);
        assertEquals(expectedMessage, failure.getMessage());
    }
}
