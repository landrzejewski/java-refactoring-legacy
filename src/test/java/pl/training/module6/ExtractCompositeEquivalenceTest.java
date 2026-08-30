package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class ExtractCompositeEquivalenceTest {
    @Test
    void extractedSuperclassCentralizesChildStorageAndAccumulation() {
        var beforeRelease = new pl.training.module6.extractcomposite.before
                .LegacyPlanNodes.ReleaseGroup();
        beforeRelease.add(new pl.training.module6.extractcomposite.before
                .LegacyPlanNodes.TaskNode(5));
        beforeRelease.add(new pl.training.module6.extractcomposite.before
                .LegacyPlanNodes.TaskNode(8));
        var afterRelease = new pl.training.module6.extractcomposite.after
                .PlanNodes.ReleaseGroup();
        afterRelease.add(new pl.training.module6.extractcomposite.after
                .PlanNodes.TaskNode(5));
        afterRelease.add(new pl.training.module6.extractcomposite.after
                .PlanNodes.TaskNode(8));

        assertEquals(beforeRelease.totalMinutes(), afterRelease.totalMinutes());
        assertEquals(beforeRelease.children().size(), afterRelease.children().size());
    }

    @Test
    void extractedCompositeDefensivelyExposesChildren() {
        var group = new pl.training.module6.extractcomposite.after
                .PlanNodes.RollbackGroup();
        group.add(new pl.training.module6.extractcomposite.after
                .PlanNodes.TaskNode(3));

        assertThrows(
                UnsupportedOperationException.class,
                () -> group.children().clear());
    }
}
