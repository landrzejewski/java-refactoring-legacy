package pl.training.module6;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module6.composite.after.DeploymentGroup;
import pl.training.module6.composite.after.DeploymentTask;
import pl.training.module6.composite.after.LegacyPathPlanMapper;
import pl.training.module6.composite.after.PlanBuilder;
import pl.training.module6.composite.after.PlanComponent;
import pl.training.module6.composite.after.PlanExecutor;
import pl.training.module6.composite.after.TotalMinutesVisitor;
import pl.training.module6.composite.before.LegacyPathPlan;
import pl.training.module6.composite.before.LegacyPlanExecutor;

final class CompositeRefactoringsTest {
    @Test
    void explicitCompositePreservesTheImplicitPathTree() {
        List<LegacyPathPlan.Entry> entries = List.of(
                new LegacyPathPlan.Entry("release/database/backup", 5),
                new LegacyPathPlan.Entry("release/database/migrate", 8),
                new LegacyPathPlan.Entry("release/deploy", 3));
        var legacy = new LegacyPathPlan(entries);
        DeploymentGroup plan = new LegacyPathPlanMapper()
                .map("release", entries);
        List<DeploymentTask> tasks = new ArrayList<>();
        plan.collectTasks(tasks);

        assertEquals(16, legacy.totalMinutes());
        assertEquals(16, plan.totalMinutes());
        assertEquals(
                List.of("backup", "migrate", "deploy"),
                tasks.stream().map(DeploymentTask::name).toList());
        assertEquals(legacy.totalMinutes(), plan.totalMinutes());
        assertEquals(
                legacy.taskNamesBelow("release"),
                tasks.stream().map(DeploymentTask::name).toList());
        assertEquals(
                List.of("database", "deploy"),
                plan.components().stream().map(PlanComponent::name).toList());
        DeploymentGroup database = assertInstanceOf(
                DeploymentGroup.class, plan.components().get(0));
        assertEquals(
                List.of("backup", "migrate"),
                database.components().stream().map(PlanComponent::name).toList());
        List<DeploymentTask> databaseTasks = new ArrayList<>();
        database.collectTasks(databaseTasks);
        assertEquals(
                legacy.taskNamesBelow("release/database"),
                databaseTasks.stream().map(DeploymentTask::name).toList());
    }

    @Test
    void pathMapperRejectsAmbiguousOrOrderChangingInput() {
        var mapper = new LegacyPathPlanMapper();

        assertThrows(
                IllegalArgumentException.class,
                () -> new LegacyPathPlan.Entry("release/ /task", 1));
        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map("release", List.of(
                        new LegacyPathPlan.Entry("release/database", 1),
                        new LegacyPathPlan.Entry(
                                "release/database/migrate", 8))));
        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map("release", List.of(
                        new LegacyPathPlan.Entry("release/a/first", 1),
                        new LegacyPathPlan.Entry("release/b/second", 1),
                        new LegacyPathPlan.Entry("release/a/third", 1))));
        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.map("release", List.of(
                        new LegacyPathPlan.Entry("other/deploy", 1))));
    }

    @Test
    void collectingParameterAndVisitorAccumulateTheSameTree() {
        DeploymentGroup plan = samplePlan();
        List<DeploymentTask> tasks = new ArrayList<>(List.of(
                new DeploymentTask("already-collected", 1)));

        plan.collectTasks(tasks);

        assertEquals(List.of(
                        "already-collected", "backup", "migrate", "deploy"),
                tasks.stream().map(DeploymentTask::name).toList());
        assertEquals(plan.totalMinutes(), plan.accept(new TotalMinutesVisitor()));
    }

    @Test
    void oneContractHandlesOneTaskAndAGroup() {
        var before = new LegacyPlanExecutor();
        var after = new PlanExecutor();
        var firstBefore = new LegacyPlanExecutor.Task("backup");
        var secondBefore = new LegacyPlanExecutor.Task("migrate");
        var firstAfter = new DeploymentTask("backup", 5);
        var secondAfter = new DeploymentTask("migrate", 8);

        assertEquals(before.execute(firstBefore), after.execute(firstAfter));
        assertEquals(
                before.executeAll(List.of(firstBefore, secondBefore)),
                after.execute(new DeploymentGroup(
                        "database", List.of(firstAfter, secondAfter))));
    }

    @Test
    void builderIsSingleUseAndBuiltCompositeIsImmutable() {
        PlanBuilder builder = PlanBuilder.group("release").task("deploy", 3);
        DeploymentGroup plan = builder.build();

        assertThrows(IllegalStateException.class, () -> builder.task("verify", 2));
        assertThrows(IllegalStateException.class, () -> builder.task(" ", -1));
        assertThrows(
                UnsupportedOperationException.class,
                () -> plan.components().add(new DeploymentTask("verify", 2)));
    }

    @Test
    void bothAccumulationImplementationsDetectOverflow() {
        PlanComponent overflowing = new DeploymentGroup("release", List.of(
                new DeploymentTask("first", Long.MAX_VALUE),
                new DeploymentTask("second", 1)));

        assertThrows(ArithmeticException.class, overflowing::totalMinutes);
        assertThrows(
                ArithmeticException.class,
                () -> overflowing.accept(new TotalMinutesVisitor()));
    }

    @Test
    void emptyCompositeHasNeutralBehavior() {
        DeploymentGroup empty = PlanBuilder.group("empty").build();
        DeploymentGroup mappedEmpty = new LegacyPathPlanMapper()
                .map("empty", List.of());

        assertEquals(0, empty.totalMinutes());
        assertEquals(empty, mappedEmpty);
        assertEquals(0L, empty.accept(new TotalMinutesVisitor()));
        assertEquals(List.of(), new PlanExecutor().execute(empty));
    }

    private static DeploymentGroup samplePlan() {
        return PlanBuilder.group("release")
                .group("database", group -> group
                        .task("backup", 5)
                        .task("migrate", 8))
                .task("deploy", 3)
                .build();
    }
}
