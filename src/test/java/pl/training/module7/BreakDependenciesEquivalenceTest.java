package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import pl.training.module7.breakdependencies.DeploymentDecision;
import pl.training.module7.breakdependencies.after.DeploymentWindowService;
import pl.training.module7.breakdependencies.after.StandardMaintenanceWindows;
import pl.training.module7.breakdependencies.before.LegacyDeploymentWindowService;

final class BreakDependenciesEquivalenceTest {
    @Test
    void standardDependencyPreservesDecisionsForEveryHour() {
        var before = new LegacyDeploymentWindowService();
        var after = new DeploymentWindowService(
                new StandardMaintenanceWindows());

        for (int hourUtc = 0; hourUtc < 24; hourUtc++) {
            assertEquals(
                    before.schedule("payments", hourUtc),
                    after.schedule("payments", hourUtc),
                    "Different decision for hour " + hourUtc);
        }
    }

    @Test
    void preservesMaintenanceWindowBoundaries() {
        var service = new DeploymentWindowService(
                new StandardMaintenanceWindows());

        assertEquals(DeploymentDecision.ALLOWED,
                service.schedule("payments", 0));
        assertEquals(DeploymentDecision.ALLOWED,
                service.schedule("payments", 5));
        assertEquals(DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW,
                service.schedule("payments", 6));
        assertEquals(DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW,
                service.schedule("payments", 23));
    }

    @Test
    void injectedSeamControlsTheDecisionAndReceivesTheArgumentsOnce() {
        var calls = new AtomicInteger();
        var receivedService = new AtomicReference<String>();
        var receivedHour = new AtomicInteger(-1);
        var service = new DeploymentWindowService((candidate, hourUtc) -> {
            calls.incrementAndGet();
            receivedService.set(candidate);
            receivedHour.set(hourUtc);
            return candidate.equals("emergency") && hourUtc == 14;
        });

        assertEquals(DeploymentDecision.ALLOWED,
                service.schedule("emergency", 14));
        assertEquals(1, calls.get());
        assertEquals("emergency", receivedService.get());
        assertEquals(14, receivedHour.get());
    }

    @Test
    void preservesValidationFailuresAndTheirOrder() {
        var before = new LegacyDeploymentWindowService();
        var after = new DeploymentWindowService(
                new StandardMaintenanceWindows());

        assertSameFailure(
                () -> before.schedule(null, 2),
                () -> after.schedule(null, 2));
        assertSameFailure(
                () -> before.schedule("   ", 2),
                () -> after.schedule("   ", 2));
        assertSameFailure(
                () -> before.schedule("payments", -1),
                () -> after.schedule("payments", -1));
        assertSameFailure(
                () -> before.schedule("payments", 24),
                () -> after.schedule("payments", 24));
        assertSameFailure(
                () -> before.schedule(" ", -1),
                () -> after.schedule(" ", -1));
    }

    @Test
    void invalidRequestDoesNotReachInjectedDependency() {
        var calls = new AtomicInteger();
        var service = new DeploymentWindowService((candidate, hourUtc) -> {
            calls.incrementAndGet();
            return true;
        });

        assertThrows(NullPointerException.class,
                () -> service.schedule(null, 2));
        assertThrows(IllegalArgumentException.class,
                () -> service.schedule(" ", 2));
        assertThrows(IllegalArgumentException.class,
                () -> service.schedule("payments", -1));
        assertThrows(IllegalArgumentException.class,
                () -> service.schedule("payments", 24));
        assertThrows(IllegalArgumentException.class,
                () -> service.schedule(" ", -1));
        assertEquals(0, calls.get());
    }

    @Test
    void rejectsMissingInjectedDependency() {
        NullPointerException failure = assertThrows(
                NullPointerException.class,
                () -> new DeploymentWindowService(null));

        assertEquals("maintenanceWindows", failure.getMessage());
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
