package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module7.breakresponsibilities.DeploymentMetrics;
import pl.training.module7.breakresponsibilities.DeploymentSample;
import pl.training.module7.breakresponsibilities.after.DeploymentMetricsCalculator;
import pl.training.module7.breakresponsibilities.after.DeploymentReportFormatter;
import pl.training.module7.breakresponsibilities.after.DeploymentReportService;
import pl.training.module7.breakresponsibilities.before.LegacyDeploymentReport;

final class BreakResponsibilitiesEquivalenceTest {
    @Test
    void preservesGeneratedReport() {
        List<DeploymentSample> samples = List.of(
                new DeploymentSample(12, true),
                new DeploymentSample(18, false),
                new DeploymentSample(24, true));
        var before = new LegacyDeploymentReport();
        var after = service();

        assertEquals(
                "deployments=3;failures=1;avgLeadTimeMinutes=18",
                before.generate(samples));
        assertEquals(before.generate(samples), after.generate(samples));
    }

    @Test
    void preservesEmptyReportAndIntegerAverage() {
        var before = new LegacyDeploymentReport();
        var after = service();

        assertEquals(
                "deployments=0;failures=0;avgLeadTimeMinutes=0",
                before.generate(List.of()));
        assertEquals(before.generate(List.of()), after.generate(List.of()));

        List<DeploymentSample> samples = List.of(
                new DeploymentSample(1, true),
                new DeploymentSample(2, true));
        assertEquals(
                "deployments=2;failures=0;avgLeadTimeMinutes=1",
                before.generate(samples));
        assertEquals(before.generate(samples), after.generate(samples));
    }

    @Test
    void neitherImplementationMutatesTheInputList() {
        var samples = new ArrayList<>(List.of(
                new DeploymentSample(30, false),
                new DeploymentSample(10, true)));
        List<DeploymentSample> snapshot = List.copyOf(samples);

        new LegacyDeploymentReport().generate(samples);
        assertEquals(snapshot, samples);

        service().generate(samples);
        assertEquals(snapshot, samples);
    }

    @Test
    void preservesFailuresForMissingListAndElement() {
        var before = new LegacyDeploymentReport();
        var after = service();

        assertSameFailure(
                () -> before.generate(null),
                () -> after.generate(null));

        List<DeploymentSample> containingNull = Arrays.asList(
                new DeploymentSample(10, true), null);
        assertSameFailure(
                () -> before.generate(containingNull),
                () -> after.generate(containingNull));
    }

    @Test
    void preservesOverflowFailureWhileAccumulatingLeadTime() {
        List<DeploymentSample> samples = List.of(
                new DeploymentSample(Long.MAX_VALUE, true),
                new DeploymentSample(1, false));
        var before = new LegacyDeploymentReport();
        var after = service();

        assertSameFailure(
                () -> before.generate(samples),
                () -> after.generate(samples));
    }

    @Test
    void extractedComponentsHaveFocusedContracts() {
        var calculator = new DeploymentMetricsCalculator();
        var formatter = new DeploymentReportFormatter();
        List<DeploymentSample> samples = List.of(
                new DeploymentSample(15, true),
                new DeploymentSample(25, false));

        DeploymentMetrics metrics = calculator.calculate(samples);

        assertEquals(new DeploymentMetrics(2, 1, 20), metrics);
        assertEquals(
                "deployments=2;failures=1;avgLeadTimeMinutes=20",
                formatter.format(metrics));
    }

    @Test
    void domainValuesAndCollaboratorsRejectInvalidState() {
        IllegalArgumentException invalidSample = assertThrows(
                IllegalArgumentException.class,
                () -> new DeploymentSample(-1, true));
        IllegalArgumentException invalidFailureCount = assertThrows(
                IllegalArgumentException.class,
                () -> new DeploymentMetrics(1, 2, 10));
        IllegalArgumentException invalidEmptyAverage = assertThrows(
                IllegalArgumentException.class,
                () -> new DeploymentMetrics(0, 0, 1));
        NullPointerException missingMetrics = assertThrows(
                NullPointerException.class,
                () -> new DeploymentReportFormatter().format(null));
        NullPointerException missingCalculator = assertThrows(
                NullPointerException.class,
                () -> new DeploymentReportService(
                        null, new DeploymentReportFormatter()));
        NullPointerException missingFormatter = assertThrows(
                NullPointerException.class,
                () -> new DeploymentReportService(
                        new DeploymentMetricsCalculator(), null));

        assertEquals("leadTimeMinutes must not be negative",
                invalidSample.getMessage());
        assertEquals("failures must be between 0 and deployments",
                invalidFailureCount.getMessage());
        assertEquals("empty metrics must have zero average lead time",
                invalidEmptyAverage.getMessage());
        assertEquals("metrics", missingMetrics.getMessage());
        assertEquals("calculator", missingCalculator.getMessage());
        assertEquals("formatter", missingFormatter.getMessage());
    }

    private static DeploymentReportService service() {
        return new DeploymentReportService(
                new DeploymentMetricsCalculator(),
                new DeploymentReportFormatter());
    }

    private static void assertSameFailure(
            Runnable beforeAction,
            Runnable afterAction) {
        RuntimeException before = assertThrows(
                RuntimeException.class, beforeAction::run);
        RuntimeException after = assertThrows(
                RuntimeException.class, afterAction::run);

        assertEquals(before.getClass(), after.getClass());
        assertEquals(before.getMessage(), after.getMessage());
    }
}
