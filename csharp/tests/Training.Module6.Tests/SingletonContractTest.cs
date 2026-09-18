using System.Reflection;
using Training.Module6.Singleton;
using Training.Module6.Singleton.Before;

namespace Training.Module6.Tests;

public sealed class SingletonContractTest
{
    [Fact]
    public void EnumLimitsInstantiationWithoutChangingTheDefaultValue()
    {
        var firstLegacy = new LegacyDeploymentDefaults();
        var secondLegacy = new LegacyDeploymentDefaults();

        Assert.NotSame(firstLegacy, secondLegacy);
        // Java: DeploymentDefaults.valueOf("INSTANCE") resolves the constant by name;
        // here the instance is looked up by name and instantiation must be impossible.
        Assert.Same(
            DeploymentDefaults.Instance,
            typeof(DeploymentDefaults)
                .GetProperty("Instance", BindingFlags.Public | BindingFlags.Static)!
                .GetValue(null));
        Assert.Empty(typeof(DeploymentDefaults).GetConstructors());
        Assert.Equal(
            firstLegacy.HealthCheckTimeout,
            DeploymentDefaults.Instance.HealthCheckTimeout);
        Assert.Equal(TimeSpan.FromSeconds(30), firstLegacy.HealthCheckTimeout);
    }

    [Fact]
    public void AllParallelAccessesObserveTheSameEnumConstant()
    {
        var identities = Enumerable.Range(0, 1_000)
            .AsParallel()
            .Select(_ => DeploymentDefaults.Instance)
            .Distinct()
            .Count();

        Assert.Equal(1, identities);
    }
}
