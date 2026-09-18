using Training.Module6.Factory.After;
using Training.Module6.Factory.Before;
using ProbeKind = Training.Module6.Factory.After.DeploymentProbeFactory.ProbeKind;

namespace Training.Module6.Tests;

public sealed class FactoryEquivalenceTest
{
    [Fact]
    public void FactoryHidesConcreteClassesWithoutChangingBehavior()
    {
        var factory = new DeploymentProbeFactory();

        Assert.Equal(
            new HttpProbe("/health").Check(),
            factory.Create(ProbeKind.Http, "/health").Check());
        Assert.Equal(
            new QueueProbe("deployments").Check(),
            factory.Create(ProbeKind.Queue, "deployments").Check());
    }

    [Fact]
    public void RejectsAnInvalidTargetAtTheCreationBoundary()
    {
        var before = new LegacyProbeService();
        var after = new ProbeService(new DeploymentProbeFactory());

        Assert.Equal(
            Assert.Throws<ArgumentException>(
                () => before.Check(LegacyProbeService.ProbeKind.Http, " ")).Message,
            Assert.Throws<ArgumentException>(
                () => after.Check(ProbeKind.Http, " ")).Message);
        Assert.Equal(
            Assert.Throws<ArgumentException>(
                () => before.Check(LegacyProbeService.ProbeKind.Queue, " ")).Message,
            Assert.Throws<ArgumentException>(
                () => after.Check(ProbeKind.Queue, " ")).Message);
    }

    [Fact]
    public void FactoryPreservesFreshInstanceSemantics()
    {
        var factory = new DeploymentProbeFactory();

        Assert.NotSame(
            factory.Create(ProbeKind.Http, "/health"),
            factory.Create(ProbeKind.Http, "/health"));
    }

    [Fact]
    public void ExtractedFactoryRemovesCreationKnowledgeFromTheService()
    {
        var before = new LegacyProbeService();
        var after = new ProbeService(new DeploymentProbeFactory());

        Assert.Equal(
            before.Check(LegacyProbeService.ProbeKind.Http, "/health"),
            after.Check(ProbeKind.Http, "/health"));
        Assert.Equal(
            before.Check(LegacyProbeService.ProbeKind.Queue, "deployments"),
            after.Check(ProbeKind.Queue, "deployments"));
    }
}
