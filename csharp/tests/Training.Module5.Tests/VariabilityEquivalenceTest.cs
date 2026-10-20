using Training.Module5.Variability;
using Training.Module5.Variability.After;
using Training.Module5.Variability.Before;

namespace Training.Module5.Tests;

public sealed class VariabilityEquivalenceTest
{
    [Theory]
    [InlineData(CustomerTier.Standard)]
    [InlineData(CustomerTier.Premium)]
    [InlineData(CustomerTier.Enterprise)]
    public void StrategyAxisPreservesEveryTierCost(CustomerTier tier)
    {
        var before = new LegacyReleaseConsole(tier);
        var after = ReleaseConsole.ForTier(tier);

        foreach (var hours in new[] { 0, 1, 10, 33, 100 })
        {
            Assert.Equal(before.SupportCost(hours), after.SupportCost(hours));
        }
        Assert.Equal(
            Assert.Throws<ArgumentException>(() => before.SupportCost(-1)).Message,
            Assert.Throws<ArgumentException>(() => after.SupportCost(-1)).Message);
    }

    [Fact]
    public void StrategyCanBeSuppliedByTheClientWithoutTouchingTheConsole()
    {
        var flatRate = new ReleaseConsole(_ => 1_000);

        Assert.Equal(1_000, flatRate.SupportCost(7));
        Assert.Equal(1_300, new ReleaseConsole(SupportCostPolicies.Premium).SupportCost(10));
    }

    [Fact]
    public void CommandAxisPreservesQueueLogAndUndo()
    {
        var before = new LegacyReleaseConsole(CustomerTier.Standard);
        var after = ReleaseConsole.ForTier(CustomerTier.Standard);

        foreach (var action in new[] { "scale-up", "maintenance", "scale-down", "scale-down" })
        {
            before.Submit(action);
            after.Submit(action);
        }
        Assert.Equal(before.Log, after.Log);
        before.RunQueued();
        after.RunQueued();
        before.UndoLast();
        after.UndoLast();
        before.UndoLast();
        after.UndoLast();

        Assert.Equal(
            [
                "scale-up -> instances=3, maintenance=off",
                "maintenance -> instances=3, maintenance=on",
                "scale-down -> instances=2, maintenance=on",
                "scale-down -> instances=1, maintenance=on",
                "undo scale-down -> instances=2, maintenance=on",
                "undo scale-down -> instances=3, maintenance=on"
            ],
            after.Log);
        Assert.Equal(before.Log, after.Log);
    }

    [Fact]
    public void CommandAxisPreservesFailures()
    {
        AssertSameFailure(console => console.Submit("reboot"), console => console.Submit("reboot"));
        AssertSameFailure(console => console.UndoLast(), console => console.UndoLast());
        AssertSameFailure(
            console =>
            {
                console.Submit("scale-down");
                console.Submit("scale-down");
                console.RunQueued();
            },
            console =>
            {
                console.Submit("scale-down");
                console.Submit("scale-down");
                console.RunQueued();
            });
    }

    [Fact]
    public void StateAxisPreservesLifecycleAndBanners()
    {
        var before = new LegacyReleaseConsole(CustomerTier.Standard);
        var after = ReleaseConsole.ForTier(CustomerTier.Standard);
        AssertSameLifecycle(before, after);

        before.Advance();
        after.Advance();
        AssertSameLifecycle(before, after);
        before.Advance();
        after.Advance();
        AssertSameLifecycle(before, after);
        AssertSameFailure(before, after, console => console.Advance(), console => console.Advance());
        before.Rollback();
        after.Rollback();
        AssertSameLifecycle(before, after);
        AssertSameFailure(before, after, console => console.Rollback(), console => console.Rollback());
        AssertSameFailure(before, after, console => console.Advance(), console => console.Advance());
    }

    [Fact]
    public void PlannedReleaseCannotBeRolledBackInBothVersions()
    {
        AssertSameFailure(console => console.Rollback(), console => console.Rollback());
    }

    private static void AssertSameLifecycle(LegacyReleaseConsole before, ReleaseConsole after)
    {
        Assert.Equal(before.Status, after.Status);
        Assert.Equal(before.Banner, after.Banner);
    }

    private static void AssertSameFailure(
        Action<LegacyReleaseConsole> beforeAction,
        Action<ReleaseConsole> afterAction)
    {
        AssertSameFailure(
            new LegacyReleaseConsole(CustomerTier.Standard),
            ReleaseConsole.ForTier(CustomerTier.Standard),
            beforeAction,
            afterAction);
    }

    private static void AssertSameFailure(
        LegacyReleaseConsole before,
        ReleaseConsole after,
        Action<LegacyReleaseConsole> beforeAction,
        Action<ReleaseConsole> afterAction)
    {
        var beforeFailure = Assert.ThrowsAny<Exception>(() => beforeAction(before));
        var afterFailure = Assert.ThrowsAny<Exception>(() => afterAction(after));

        Assert.Equal(beforeFailure.GetType(), afterFailure.GetType());
        Assert.Equal(beforeFailure.Message, afterFailure.Message);
        Assert.Equal(before.Status, after.Status);
        Assert.Equal(before.Log, after.Log);
    }
}
