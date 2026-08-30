package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import pl.training.module7.middleman.DeploymentStatus;
import pl.training.module7.middleman.before.ReleaseService;

final class MiddleManEquivalenceTest {
    @Test
    void directCollaborationPreservesLookupUpdatesOverwriteAndRendering() {
        var beforeRegistry =
                new pl.training.module7.middleman.before.DeploymentRegistry();
        var beforeService = new ReleaseService(beforeRegistry);
        var beforeDashboard =
                new pl.training.module7.middleman.before.ReleaseDashboard(
                        beforeService);

        var afterRegistry =
                new pl.training.module7.middleman.after.DeploymentRegistry();
        var afterDashboard =
                new pl.training.module7.middleman.after.ReleaseDashboard(
                        afterRegistry);

        assertEquals(beforeService.statusOf("dep-42"), afterRegistry.statusOf("dep-42"));
        assertEquals(DeploymentStatus.UNKNOWN, afterRegistry.statusOf("dep-42"));
        assertEquals(beforeDashboard.render("dep-42"), afterDashboard.render("dep-42"));

        beforeService.update("dep-42", DeploymentStatus.RUNNING);
        afterRegistry.update("dep-42", DeploymentStatus.RUNNING);
        assertEquals(beforeService.statusOf("dep-42"), afterRegistry.statusOf("dep-42"));
        assertEquals(beforeDashboard.render("dep-42"), afterDashboard.render("dep-42"));

        beforeService.update("dep-42", DeploymentStatus.SUCCEEDED);
        afterRegistry.update("dep-42", DeploymentStatus.SUCCEEDED);
        assertEquals(beforeService.statusOf("dep-42"), afterRegistry.statusOf("dep-42"));
        assertEquals("dep-42 -> SUCCEEDED", afterDashboard.render("dep-42"));
        assertEquals(beforeDashboard.render("dep-42"), afterDashboard.render("dep-42"));
    }

    @Test
    void validationRemainsAtTheRegistryBoundary() {
        var beforeRegistry =
                new pl.training.module7.middleman.before.DeploymentRegistry();
        var beforeService = new ReleaseService(beforeRegistry);
        var beforeDashboard =
                new pl.training.module7.middleman.before.ReleaseDashboard(
                        beforeService);

        var afterRegistry =
                new pl.training.module7.middleman.after.DeploymentRegistry();
        var afterDashboard =
                new pl.training.module7.middleman.after.ReleaseDashboard(
                        afterRegistry);

        for (String invalidId : new String[] {null, "", "  \t"}) {
            assertEquals(
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> beforeService.statusOf(invalidId))
                            .getMessage(),
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> afterRegistry.statusOf(invalidId))
                            .getMessage());
            assertEquals(
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> beforeDashboard.render(invalidId))
                            .getMessage(),
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> afterDashboard.render(invalidId))
                            .getMessage());
        }

        assertEquals(
                assertThrows(
                        NullPointerException.class,
                        () -> beforeService.update("dep-42", null))
                        .getMessage(),
                assertThrows(
                        NullPointerException.class,
                        () -> afterRegistry.update("dep-42", null))
                        .getMessage());
        assertEquals(DeploymentStatus.UNKNOWN, beforeService.statusOf("dep-42"));
        assertEquals(DeploymentStatus.UNKNOWN, afterRegistry.statusOf("dep-42"));
    }
}
