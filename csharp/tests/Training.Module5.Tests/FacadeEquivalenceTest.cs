using Training.Module5.Facade.After;
using Training.Module5.Facade.Before;
using ArtifactStore = Training.Module5.Facade.Before.ArtifactStore;
using ConfigRenderer = Training.Module5.Facade.Before.ConfigRenderer;
using DeploymentQueue = Training.Module5.Facade.Before.DeploymentQueue;
using ReleaseNotifier = Training.Module5.Facade.Before.ReleaseNotifier;

namespace Training.Module5.Tests;

public sealed class FacadeEquivalenceTest
{
    [Fact]
    public void CliProducesTheSameEffectsInTheSameOrder()
    {
        var before = new List<string>();
        var after = new List<string>();
        var legacyCli = new LegacyReleaseCli(
            new ArtifactStore(before.Add),
            new ConfigRenderer(before.Add),
            new DeploymentQueue(before.Add),
            new ReleaseNotifier(before.Add));
        var cli = new ReleaseCli(new ReleasePublishingFacade(after.Add));

        Assert.Equal(legacyCli.Publish("rel-42", " PROD "), cli.Publish("rel-42", " PROD "));
        Assert.Equal(
            [
                "stored rel-42.jar",
                "rendered rel-42/prod.yaml",
                "queued rel-42.jar with rel-42/prod.yaml",
                "announced rel-42 on prod"
            ],
            after);
        Assert.Equal(before, after);
    }

    [Fact]
    public void SchedulerProducesTheSameEffectsInTheSameOrder()
    {
        var before = new List<string>();
        var after = new List<string>();
        var legacyScheduler = new LegacyReleaseScheduler(
            new ArtifactStore(before.Add),
            new ConfigRenderer(before.Add),
            new DeploymentQueue(before.Add),
            new ReleaseNotifier(before.Add));
        var scheduler = new ReleaseScheduler(new ReleasePublishingFacade(after.Add));

        Assert.Equal(
            legacyScheduler.PublishNightly(["rel-1", "rel-2"]),
            scheduler.PublishNightly(["rel-1", "rel-2"]));
        Assert.Equal(before, after);
    }

    [Fact]
    public void InvalidRequestsAreRejectedBeforeAnySideEffect()
    {
        var before = new List<string>();
        var after = new List<string>();
        var legacyCli = new LegacyReleaseCli(
            new ArtifactStore(before.Add),
            new ConfigRenderer(before.Add),
            new DeploymentQueue(before.Add),
            new ReleaseNotifier(before.Add));
        var facade = new ReleasePublishingFacade(after.Add);

        foreach (var (releaseId, environment) in new[] { ("rel-1", "qa"), (" ", "prod") })
        {
            Assert.Equal(
                Assert.Throws<ArgumentException>(
                    () => legacyCli.Publish(releaseId, environment)).Message,
                Assert.Throws<ArgumentException>(
                    () => facade.Publish(releaseId, environment)).Message);
        }
        Assert.Empty(before);
        Assert.Empty(after);
    }

    [Fact]
    public void SubsystemsAreHiddenBehindTheFacade()
    {
        // Java: package-private classes; C#: internal (Type.IsPublic == false).
        foreach (var name in new[] { "ArtifactStore", "ConfigRenderer", "DeploymentQueue", "ReleaseNotifier" })
        {
            var subsystem = typeof(ReleasePublishingFacade).Assembly
                .GetType("Training.Module5.Facade.After." + name, throwOnError: true)!;
            Assert.False(subsystem.IsPublic, name);
        }
    }
}
