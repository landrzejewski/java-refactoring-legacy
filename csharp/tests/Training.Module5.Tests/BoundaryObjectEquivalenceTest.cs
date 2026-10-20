using Training.Module5.BoundaryObject;
using Training.Module5.BoundaryObject.After;
using Training.Module5.BoundaryObject.Before;

namespace Training.Module5.Tests;

public sealed class BoundaryObjectEquivalenceTest
{
    private static readonly IReadOnlyDictionary<string, IReadOnlyDictionary<string, string>> Builds =
        new Dictionary<string, IReadOnlyDictionary<string, string>>
        {
            ["build-1"] = Payload("build-1", "SUCCESS", "82.5", "0", "A1B2C3D"),
            ["build-2"] = Payload("build-2", " success ", "80", "0", "a1b2c3d4e5"),
            ["build-3"] = Payload("build-3", "SUCCESS", "79.9", "0", "abcdef0"),
            ["build-4"] = Payload("build-4", "FAILED", "95", "3", "abcdef1"),
            ["build-5"] = Payload("build-5", "running", "0", "0", "abcdef2")
        };

    private readonly LegacyCiClient _ci = buildId => Builds[buildId];

    [Fact]
    public void DomainDecisionsAreTheSameForEveryValidPayload()
    {
        var before = new LegacyReleaseGate(_ci);
        var after = new ReleaseGate(new CiBuildReportsAdapter(_ci));

        foreach (var buildId in Builds.Keys)
        {
            Assert.True(before.CanRelease(buildId) == after.CanRelease(buildId), buildId);
            Assert.Equal(before.Summary(buildId), after.Summary(buildId));
        }
        Assert.Equal(
            "build-1 [a1b2c3d] SUCCESS coverage=82.5% failed=0",
            after.Summary("build-1"));
        Assert.Equal("build-2 [a1b2c3d4e5] SUCCESS coverage=80.0% failed=0", after.Summary("build-2"));
        Assert.Equal(
            [true, true, false, false, false],
            Builds.Keys.Order(StringComparer.Ordinal).Select(after.CanRelease).ToList());
    }

    [Theory]
    [InlineData("status")]
    [InlineData("coverage")]
    [InlineData("failed")]
    [InlineData("commit")]
    [InlineData("id")]
    public void PayloadWithMissingFieldIsRejectedAtTheBoundary(string missing)
    {
        var broken = new Dictionary<string, string>(Builds["build-1"]);
        broken.Remove(missing);
        var adapter = new CiBuildReportsAdapter(_ => broken);

        var failure = Assert.Throws<ArgumentException>(() => adapter.Find("build-1"));

        Assert.Equal("invalid CI payload: missing " + missing, failure.Message);
    }

    [Fact]
    public void MalformedValuesAreRejectedAtTheBoundaryInsteadOfInsideTheDomain()
    {
        var adapter = new CiBuildReportsAdapter(_ =>
            Payload("build-9", "SUCCESS", "eighty", "0", "abcdef0"));
        var legacy = new LegacyReleaseGate(_ =>
            Payload("build-9", "SUCCESS", "eighty", "0", "abcdef0"));

        // Java: NumberFormatException; .NET parsing throws FormatException.
        Assert.Throws<FormatException>(() => legacy.CanRelease("build-9"));
        Assert.Equal(
            "invalid CI payload: The input string 'eighty' was not in a correct format.",
            Assert.Throws<ArgumentException>(() => adapter.Find("build-9")).Message);
        Assert.Equal(
            "invalid CI payload: coverage must be between 0 and 100",
            Assert.Throws<ArgumentException>(
                () => new CiBuildReportsAdapter(_ =>
                    Payload("b", "SUCCESS", "120", "0", "abcdef0")).Find("b")).Message);
    }

    [Fact]
    public void BoundaryObjectValidatesItselfIndependentlyOfThePayload()
    {
        Assert.Throws<ArgumentException>(
            () => new BuildReport("b", Status.Success, 50, -1, "abcdef0"));
        Assert.Throws<ArgumentException>(
            () => new BuildReport("b", Status.Success, 50, 0, "not-a-sha"));
    }

    private static IReadOnlyDictionary<string, string> Payload(
        string id, string status, string coverage, string failed, string commit) =>
        new Dictionary<string, string>
        {
            ["id"] = id,
            ["status"] = status,
            ["coverage"] = coverage,
            ["failed"] = failed,
            ["commit"] = commit
        };
}
