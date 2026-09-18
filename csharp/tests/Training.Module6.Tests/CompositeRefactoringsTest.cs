using Training.Module6.Composite.After;
using Training.Module6.Composite.Before;

namespace Training.Module6.Tests;

public sealed class CompositeRefactoringsTest
{
    [Fact]
    public void ExplicitCompositePreservesTheImplicitPathTree()
    {
        List<LegacyPathPlan.Entry> entries =
        [
            new("release/database/backup", 5),
            new("release/database/migrate", 8),
            new("release/deploy", 3)
        ];
        var legacy = new LegacyPathPlan(entries);
        var plan = new LegacyPathPlanMapper().Map("release", entries);
        var tasks = new List<DeploymentTask>();
        plan.CollectTasks(tasks);

        Assert.Equal(16, legacy.TotalMinutes());
        Assert.Equal(16, plan.TotalMinutes());
        Assert.Equal(["backup", "migrate", "deploy"], tasks.Select(task => task.Name));
        Assert.Equal(legacy.TotalMinutes(), plan.TotalMinutes());
        Assert.Equal(legacy.TaskNamesBelow("release"), tasks.Select(task => task.Name));
        Assert.Equal(["database", "deploy"], plan.Components.Select(component => component.Name));
        var database = Assert.IsType<DeploymentGroup>(plan.Components[0]);
        Assert.Equal(["backup", "migrate"], database.Components.Select(component => component.Name));
        var databaseTasks = new List<DeploymentTask>();
        database.CollectTasks(databaseTasks);
        Assert.Equal(
            legacy.TaskNamesBelow("release/database"),
            databaseTasks.Select(task => task.Name));
    }

    [Fact]
    public void PathMapperRejectsAmbiguousOrOrderChangingInput()
    {
        var mapper = new LegacyPathPlanMapper();

        Assert.Throws<ArgumentException>(() => new LegacyPathPlan.Entry("release/ /task", 1));
        Assert.Throws<ArgumentException>(() => mapper.Map("release",
        [
            new LegacyPathPlan.Entry("release/database", 1),
            new LegacyPathPlan.Entry("release/database/migrate", 8)
        ]));
        Assert.Throws<ArgumentException>(() => mapper.Map("release",
        [
            new LegacyPathPlan.Entry("release/a/first", 1),
            new LegacyPathPlan.Entry("release/b/second", 1),
            new LegacyPathPlan.Entry("release/a/third", 1)
        ]));
        Assert.Throws<ArgumentException>(() => mapper.Map("release",
        [
            new LegacyPathPlan.Entry("other/deploy", 1)
        ]));
    }

    [Fact]
    public void CollectingParameterAndVisitorAccumulateTheSameTree()
    {
        var plan = SamplePlan();
        var tasks = new List<DeploymentTask> { new("already-collected", 1) };

        plan.CollectTasks(tasks);

        Assert.Equal(
            ["already-collected", "backup", "migrate", "deploy"],
            tasks.Select(task => task.Name));
        Assert.Equal(plan.TotalMinutes(), plan.Accept(new TotalMinutesVisitor()));
    }

    [Fact]
    public void OneContractHandlesOneTaskAndAGroup()
    {
        var before = new LegacyPlanExecutor();
        var after = new PlanExecutor();
        var firstBefore = new LegacyPlanExecutor.Task("backup");
        var secondBefore = new LegacyPlanExecutor.Task("migrate");
        var firstAfter = new DeploymentTask("backup", 5);
        var secondAfter = new DeploymentTask("migrate", 8);

        Assert.Equal(before.Execute(firstBefore), after.Execute(firstAfter));
        Assert.Equal(
            before.ExecuteAll([firstBefore, secondBefore]),
            after.Execute(new DeploymentGroup("database", [firstAfter, secondAfter])));
    }

    [Fact]
    public void BuilderIsSingleUseAndBuiltCompositeIsImmutable()
    {
        var builder = PlanBuilder.Group("release").Task("deploy", 3);
        var plan = builder.Build();

        Assert.Throws<InvalidOperationException>(() => builder.Task("verify", 2));
        Assert.Throws<InvalidOperationException>(() => builder.Task(" ", -1));
        Assert.Throws<NotSupportedException>(
            () => ((ICollection<PlanComponent>)plan.Components).Add(new DeploymentTask("verify", 2)));
    }

    [Fact]
    public void BothAccumulationImplementationsDetectOverflow()
    {
        PlanComponent overflowing = new DeploymentGroup("release",
        [
            new DeploymentTask("first", long.MaxValue),
            new DeploymentTask("second", 1)
        ]);

        Assert.Throws<OverflowException>(() => overflowing.TotalMinutes());
        Assert.Throws<OverflowException>(() => overflowing.Accept(new TotalMinutesVisitor()));
    }

    [Fact]
    public void EmptyCompositeHasNeutralBehavior()
    {
        var empty = PlanBuilder.Group("empty").Build();
        var mappedEmpty = new LegacyPathPlanMapper().Map("empty", []);

        Assert.Equal(0, empty.TotalMinutes());
        Assert.Equal(empty, mappedEmpty);
        Assert.Equal(0L, empty.Accept(new TotalMinutesVisitor()));
        Assert.Empty(new PlanExecutor().Execute(empty));
    }

    private static DeploymentGroup SamplePlan() =>
        PlanBuilder.Group("release")
            .Group("database", group => group
                .Task("backup", 5)
                .Task("migrate", 8))
            .Task("deploy", 3)
            .Build();
}
