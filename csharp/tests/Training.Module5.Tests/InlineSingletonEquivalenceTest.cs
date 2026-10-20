using Training.Module5.InlineSingleton.After;
using Training.Module5.InlineSingleton.Before;

namespace Training.Module5.Tests;

// Java: @ResourceLock("LegacyFeatureFlags"). Only this class touches the legacy singleton
// and xUnit runs the tests of one class sequentially, so no extra collection is needed.
public sealed class InlineSingletonEquivalenceTest
{
    private static readonly IReadOnlyList<string[]> Combinations =
    [
        [],
        ["canary"],
        ["dark-launch"],
        ["canary", "dark-launch"],
        ["unrelated"]
    ];

    [Fact]
    public void PlanIsTheSameForEveryFlagCombination()
    {
        var legacy = new LegacyRolloutService();
        try
        {
            foreach (var combination in Combinations)
            {
                // stary kod: trzeba zmienić globalny stan i pamiętać o sprzątaniu
                LegacyFeatureFlags.Instance.Reset();
                foreach (var flag in combination)
                {
                    LegacyFeatureFlags.Instance.Enable(flag);
                }

                Assert.Equal(
                    legacy.Plan("rel-42"),
                    new RolloutService(new FeatureFlags(combination)).Plan("rel-42"));
            }
        }
        finally
        {
            LegacyFeatureFlags.Instance.Reset();
        }
    }

    [Fact]
    public void ServicesWithDifferentFlagsCanCoexistWithoutSharedState()
    {
        var canary = new RolloutService(FeatureFlags.Of("canary"));
        var plain = new RolloutService(FeatureFlags.Of());

        Assert.Equal("rel-1: 10% -> 50% -> 100%", canary.Plan("rel-1"));
        Assert.Equal("rel-1: 100%", plain.Plan("rel-1"));
        Assert.Equal("rel-1: 10% -> 50% -> 100%", canary.Plan("rel-1"));
    }

    [Fact]
    public void LegacySingletonLeaksStateBetweenUsers()
    {
        var first = new LegacyRolloutService();
        var second = new LegacyRolloutService();
        try
        {
            LegacyFeatureFlags.Instance.Enable("canary");

            Assert.Equal("rel-1: 10% -> 50% -> 100%", second.Plan("rel-1"));
            Assert.Equal(first.Plan("rel-1"), second.Plan("rel-1"));
        }
        finally
        {
            LegacyFeatureFlags.Instance.Reset();
        }
    }

    [Fact]
    public void CompositionRootBuildsTheServiceFromConfiguration()
    {
        Assert.Equal(
            "rel-7: 10% -> 50% -> 100% (dark launch)",
            RolloutCompositionRoot.Create(" canary , dark-launch ").Plan("rel-7"));
        Assert.Equal("rel-7: 100%", RolloutCompositionRoot.Create("").Plan("rel-7"));
    }

    [Fact]
    public void FlagsAreImmutableValues()
    {
        var source = new HashSet<string> { "canary" };
        var flags = new FeatureFlags(source);
        source.Clear();

        Assert.Equal(FeatureFlags.Of("canary"), flags);
        // Java: Set.copyOf(...).add throws UnsupportedOperationException.
        Assert.Throws<NotSupportedException>(() => ((ICollection<string>)flags.Enabled).Add("x"));
        Assert.Equal(
            Assert.Throws<ArgumentException>(
                () => new LegacyRolloutService().Plan(" ")).Message,
            Assert.Throws<ArgumentException>(
                () => new RolloutService(FeatureFlags.Of()).Plan(" ")).Message);
    }
}
