package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module5.facade.after.ReleaseCli;
import pl.training.module5.facade.after.ReleasePublishingFacade;
import pl.training.module5.facade.after.ReleaseScheduler;
import pl.training.module5.facade.before.ArtifactStore;
import pl.training.module5.facade.before.ConfigRenderer;
import pl.training.module5.facade.before.DeploymentQueue;
import pl.training.module5.facade.before.LegacyReleaseCli;
import pl.training.module5.facade.before.LegacyReleaseScheduler;
import pl.training.module5.facade.before.ReleaseNotifier;

final class FacadeEquivalenceTest {
    @Test
    void cliProducesTheSameEffectsInTheSameOrder() {
        List<String> before = new ArrayList<>();
        List<String> after = new ArrayList<>();
        var legacyCli = new LegacyReleaseCli(
                new ArtifactStore(before::add),
                new ConfigRenderer(before::add),
                new DeploymentQueue(before::add),
                new ReleaseNotifier(before::add));
        var cli = new ReleaseCli(new ReleasePublishingFacade(after::add));

        assertEquals(legacyCli.publish("rel-42", " PROD "), cli.publish("rel-42", " PROD "));
        assertEquals(
                List.of(
                        "stored rel-42.jar",
                        "rendered rel-42/prod.yaml",
                        "queued rel-42.jar with rel-42/prod.yaml",
                        "announced rel-42 on prod"),
                after);
        assertEquals(before, after);
    }

    @Test
    void schedulerProducesTheSameEffectsInTheSameOrder() {
        List<String> before = new ArrayList<>();
        List<String> after = new ArrayList<>();
        var legacyScheduler = new LegacyReleaseScheduler(
                new ArtifactStore(before::add),
                new ConfigRenderer(before::add),
                new DeploymentQueue(before::add),
                new ReleaseNotifier(before::add));
        var scheduler = new ReleaseScheduler(new ReleasePublishingFacade(after::add));

        assertEquals(
                legacyScheduler.publishNightly(List.of("rel-1", "rel-2")),
                scheduler.publishNightly(List.of("rel-1", "rel-2")));
        assertEquals(before, after);
    }

    @Test
    void invalidRequestsAreRejectedBeforeAnySideEffect() {
        List<String> before = new ArrayList<>();
        List<String> after = new ArrayList<>();
        var legacyCli = new LegacyReleaseCli(
                new ArtifactStore(before::add),
                new ConfigRenderer(before::add),
                new DeploymentQueue(before::add),
                new ReleaseNotifier(before::add));
        var facade = new ReleasePublishingFacade(after::add);

        for (String[] request : new String[][] {{"rel-1", "qa"}, {" ", "prod"}}) {
            assertEquals(
                    assertThrows(IllegalArgumentException.class,
                            () -> legacyCli.publish(request[0], request[1])).getMessage(),
                    assertThrows(IllegalArgumentException.class,
                            () -> facade.publish(request[0], request[1])).getMessage());
        }
        assertEquals(List.of(), before);
        assertEquals(List.of(), after);
    }

    @Test
    void subsystemsAreHiddenBehindTheFacade() throws ClassNotFoundException {
        for (String name : List.of("ArtifactStore", "ConfigRenderer", "DeploymentQueue", "ReleaseNotifier")) {
            Class<?> subsystem = Class.forName("pl.training.module5.facade.after." + name);
            assertFalse(Modifier.isPublic(subsystem.getModifiers()), name);
        }
    }
}
