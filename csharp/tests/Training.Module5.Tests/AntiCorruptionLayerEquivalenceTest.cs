using System.Reflection;
using Training.Module5.AntiCorruptionLayer;
using Training.Module5.AntiCorruptionLayer.After;
using Training.Module5.AntiCorruptionLayer.After.Acl;
using Training.Module5.AntiCorruptionLayer.Before;
using Environment = Training.Module5.AntiCorruptionLayer.After.Environment;

namespace Training.Module5.Tests;

public sealed class AntiCorruptionLayerEquivalenceTest
{
    private static readonly DateOnly Today = new(2026, 10, 20);

    private static readonly LegacyCmdbClient Cmdb = () =>
    [
        new LegacyCmdbRecord("prod-eu", "P", 1, "20261015"),
        new LegacyCmdbRecord("test-b", "T", 1, "20260801"),
        new LegacyCmdbRecord("test-a", "T", 1, "20261019"),
        new LegacyCmdbRecord("dev-1", "D", 1, "20260901"),
        new LegacyCmdbRecord("dev-old", "D", 0, "20250101"),
        new LegacyCmdbRecord("prod-us", "P", 9, "20240101")
    ];

    [Fact]
    public void PlanningRulesGiveTheSameAnswersThroughTheAcl()
    {
        var before = new LegacyDeploymentPlanner(Cmdb);
        var after = new DeploymentPlanner(new CmdbEnvironmentDirectory(Cmdb));

        Assert.Equal(before.CanaryTargets(), after.CanaryTargets());
        Assert.Equal(before.StaleEnvironments(Today), after.StaleEnvironments(Today));
        foreach (var name in new[] { "prod-eu", "prod-us", "test-a", "unknown" })
        {
            Assert.True(before.IsProduction(name) == after.IsProduction(name), name);
        }
        Assert.Equal(["dev-1", "test-a", "test-b"], after.CanaryTargets());
        Assert.Equal(["dev-1", "test-b"], after.StaleEnvironments(Today));
    }

    [Fact]
    public void TranslatorMapsLegacyCodesToTheDomainModel()
    {
        var environments = new CmdbEnvironmentDirectory(Cmdb).All();

        Assert.Equal(
            new Environment("prod-eu", Tier.Production, true, new DateOnly(2026, 10, 15)),
            environments[0]);
        Assert.Equal(
            new Environment("prod-us", Tier.Production, false, new DateOnly(2024, 1, 1)),
            environments[^1]);
    }

    [Fact]
    public void UnknownLegacyDataIsRejectedInsideTheAcl()
    {
        var unknownCode = new CmdbEnvironmentDirectory(
            () => [new LegacyCmdbRecord("x", "Q", 1, "20260101")]);
        var badDate = new CmdbEnvironmentDirectory(
            () => [new LegacyCmdbRecord("x", "T", 1, "2026-01-01")]);

        Assert.Equal("unknown CMDB environment code: Q",
            Assert.Throws<ArgumentException>(() => unknownCode.All()).Message);
        Assert.Equal("invalid CMDB date: 2026-01-01",
            Assert.Throws<ArgumentException>(() => badDate.All()).Message);
    }

    [Fact]
    public void DomainCanBeTestedWithoutAnyLegacyType()
    {
        var directory = new FixedDirectory([new Environment("qa", Tier.Test, true, Today)]);

        Assert.Equal(["qa"], new DeploymentPlanner(directory).CanaryTargets());
        // Java: record components; C#: the record's public properties.
        Assert.DoesNotContain(
            typeof(Environment).GetProperties(BindingFlags.Public | BindingFlags.Instance),
            property => property.PropertyType.Namespace?.EndsWith("AntiCorruptionLayer", StringComparison.Ordinal) == true);
    }

    private sealed class FixedDirectory(IReadOnlyList<Environment> environments) : IEnvironmentDirectory
    {
        public IReadOnlyList<Environment> All() => environments;
    }
}
