package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.module6.factory.after.DeploymentProbeFactory;
import pl.training.module6.factory.after.DeploymentProbeFactory.ProbeKind;
import pl.training.module6.factory.after.ProbeService;
import pl.training.module6.factory.before.LegacyProbeService;

final class FactoryEquivalenceTest {
    @Test
    void factoryHidesConcreteClassesWithoutChangingBehavior() {
        var factory = new DeploymentProbeFactory();

        assertEquals(
                new pl.training.module6.factory.before.HttpProbe("/health").check(),
                factory.create(ProbeKind.HTTP, "/health").check());
        assertEquals(
                new pl.training.module6.factory.before.QueueProbe("deployments").check(),
                factory.create(ProbeKind.QUEUE, "deployments").check());
    }

    @Test
    void rejectsAnInvalidTargetAtTheCreationBoundary() {
        var before = new LegacyProbeService();
        var after = new ProbeService(new DeploymentProbeFactory());

        assertEquals(
                assertThrows(
                        IllegalArgumentException.class,
                        () -> before.check(
                                LegacyProbeService.ProbeKind.HTTP, " "))
                        .getMessage(),
                assertThrows(
                        IllegalArgumentException.class,
                        () -> after.check(ProbeKind.HTTP, " "))
                        .getMessage());
        assertEquals(
                assertThrows(
                        IllegalArgumentException.class,
                        () -> before.check(
                                LegacyProbeService.ProbeKind.QUEUE, " "))
                        .getMessage(),
                assertThrows(
                        IllegalArgumentException.class,
                        () -> after.check(ProbeKind.QUEUE, " "))
                        .getMessage());
    }

    @Test
    void factoryPreservesFreshInstanceSemantics() {
        var factory = new DeploymentProbeFactory();

        assertNotSame(
                factory.create(ProbeKind.HTTP, "/health"),
                factory.create(ProbeKind.HTTP, "/health"));
    }

    @Test
    void extractedFactoryRemovesCreationKnowledgeFromTheService() {
        var before = new LegacyProbeService();
        var after = new ProbeService(new DeploymentProbeFactory());

        assertEquals(
                before.check(LegacyProbeService.ProbeKind.HTTP, "/health"),
                after.check(ProbeKind.HTTP, "/health"));
        assertEquals(
                before.check(LegacyProbeService.ProbeKind.QUEUE, "deployments"),
                after.check(ProbeKind.QUEUE, "deployments"));
    }
}
