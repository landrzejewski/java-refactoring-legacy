using Training.Module7.BreakMethod;
using Training.Module7.BreakMethod.After;
using Training.Module7.BreakMethod.Before;
using static Training.Module7.Tests.FailureAssertions;

namespace Training.Module7.Tests;

public sealed class BreakMethodEquivalenceTest
{
    [Fact]
    public void PreservesOrderingRenderingAndTheCallersCollection()
    {
        var entries = new List<ManifestEntry>
        {
            new("web", "sha-web", 20),
            new("worker", "sha-worker", 10),
            new("api", "sha-api", 10)
        };
        var originalOrder = entries.ToList();

        var legacy = new LegacyReleaseManifestBuilder();
        var refactored = new ReleaseManifestBuilder();

        const string expected = "10|api|sha-api\n"
            + "10|worker|sha-worker\n"
            + "20|web|sha-web";

        Assert.Equal(expected, legacy.Build(entries));
        Assert.Equal(expected, refactored.Build(entries));
        Assert.Equal(originalOrder, entries);
        Assert.Equal("", legacy.Build([]));
        Assert.Equal("", refactored.Build([]));
    }

    [Fact]
    public void PreservesValidationTypeMessageAndEncounterOrder()
    {
        var legacy = new LegacyReleaseManifestBuilder();
        var refactored = new ReleaseManifestBuilder();

        AssertSameFailure(
            () => legacy.Build(null!),
            () => refactored.Build(null!));
        AssertSameFailure(
            () => legacy.Build(ListContainingNull()),
            () => refactored.Build(ListContainingNull()));
        AssertSameFailure(
            () => legacy.Build([new ManifestEntry(null!, null!, -1)]),
            () => refactored.Build([new ManifestEntry(null!, null!, -1)]));
        AssertSameFailure(
            () => legacy.Build([new ManifestEntry(" ", null!, -1)]),
            () => refactored.Build([new ManifestEntry(" ", null!, -1)]));
        AssertSameFailure(
            () => legacy.Build([new ManifestEntry("api", null!, -1)]),
            () => refactored.Build([new ManifestEntry("api", null!, -1)]));
        AssertSameFailure(
            () => legacy.Build([new ManifestEntry("api", " ", -1)]),
            () => refactored.Build([new ManifestEntry("api", " ", -1)]));
        AssertSameFailure(
            () => legacy.Build([new ManifestEntry("api", "sha", -1)]),
            () => refactored.Build([new ManifestEntry("api", "sha", -1)]));
    }

    [Fact]
    public void PreservesInputOrderWhenAllSortKeysAreEqual()
    {
        ManifestEntry[] entries =
        [
            new("api", "sha-first", 10),
            new("api", "sha-second", 10)
        ];
        const string expected = "10|api|sha-first\n10|api|sha-second";

        Assert.Equal(expected, new LegacyReleaseManifestBuilder().Build(entries));
        Assert.Equal(expected, new ReleaseManifestBuilder().Build(entries));
    }

    private static List<ManifestEntry> ListContainingNull() => [null!];
}
