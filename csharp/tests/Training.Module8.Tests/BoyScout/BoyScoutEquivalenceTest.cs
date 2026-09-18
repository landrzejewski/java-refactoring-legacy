using Training.Module8.BoyScout;

namespace Training.Module8.Tests.BoyScout;

public sealed class BoyScoutEquivalenceTest
{
    private readonly Module8.BoyScout.Before.ReleaseSummaryFormatter before = new();
    private readonly Module8.BoyScout.After.ReleaseSummaryFormatter after = new();

    public static TheoryData<string, string, List<DeploymentResult>> RepresentativeResults() =>
        new()
        {
            {
                "single success",
                "release-1",
                [Success("test", "deployed")]
            },
            {
                "single failure",
                "release-2",
                [Failure("production", "timeout")]
            },
            {
                "mixed result in stable order",
                " release-3 ",
                [
                    Success("test", "deployed"),
                    Failure("staging", "health check failed"),
                    Success("production", "deployed")
                ]
            }
        };

    [Theory]
    [MemberData(nameof(RepresentativeResults))]
    public void LocalCleanupPreservesRepresentativeOutputs(
        string description,
        string releaseId,
        List<DeploymentResult> results)
    {
        Assert.NotEmpty(description);
        Assert.Equal(
            before.Format(releaseId, results),
            after.Format(releaseId, results));
    }

    [Fact]
    public void RefactoredCodeProducesTheIndependentlySpecifiedSummary()
    {
        List<DeploymentResult> results =
        [
            Success("test", "deployed"),
            Failure("production", "timeout")
        ];

        Assert.Equal(
            "Release release-42\n"
            + "[OK] test: deployed\n"
            + "[ERROR] production: timeout\n"
            + "Successful: 1/2",
            after.Format(" release-42 ", results));
    }

    [Fact]
    public void LocalCleanupPreservesValidationFailuresAndTheirOrder()
    {
        AssertSameFailure(
            () => before.Format(null!, [Success("test", "ok")]),
            () => after.Format(null!, [Success("test", "ok")]));
        AssertSameFailure(
            () => before.Format("release-1", null!),
            () => after.Format("release-1", null!));
        AssertSameFailure(
            () => before.Format("   ", [Success("test", "ok")]),
            () => after.Format("   ", [Success("test", "ok")]));
        AssertSameFailure(
            () => before.Format("release-1", []),
            () => after.Format("release-1", []));
        AssertSameFailure(
            () => before.Format("release-1", [null!]),
            () => after.Format("release-1", [null!]));
        AssertSameFailure(
            () => before.Format(" ", null!),
            () => after.Format(" ", null!));
    }

    [Fact]
    public void BothVersionsExposeTheSameCallableContract()
    {
        SummaryContract oldContract = before.Format;
        SummaryContract cleanedContract = after.Format;
        List<DeploymentResult> results = [Success("test", "ok")];

        Assert.Equal(
            oldContract("release-1", results),
            cleanedContract("release-1", results));
    }

    [Fact]
    public void SharedInputModelRejectsIncompleteResults()
    {
        var undefinedStatus = Assert.Throws<ArgumentOutOfRangeException>(
            () => new DeploymentResult((DeploymentStatus)42, "test", "ok"));
        Assert.Equal("status", undefinedStatus.ParamName);
        AssertMissing(
            "environment",
            () => new DeploymentResult(
                DeploymentStatus.Success, null!, "ok"));
        AssertFailure(
            "environment must not be blank",
            () => new DeploymentResult(
                DeploymentStatus.Success, " ", "ok"));
        AssertMissing(
            "description",
            () => new DeploymentResult(
                DeploymentStatus.Success, "test", null!));
        AssertFailure(
            "description must not be blank",
            () => new DeploymentResult(
                DeploymentStatus.Success, "test", " "));
    }

    private static DeploymentResult Success(
        string environment,
        string description) =>
        new(DeploymentStatus.Success, environment, description);

    private static DeploymentResult Failure(
        string environment,
        string description) =>
        new(DeploymentStatus.Failure, environment, description);

    private static void AssertSameFailure(
        Action beforeAction,
        Action afterAction)
    {
        Exception beforeFailure = Assert.ThrowsAny<Exception>(beforeAction);
        Exception afterFailure = Assert.ThrowsAny<Exception>(afterAction);

        Assert.Equal(beforeFailure.GetType(), afterFailure.GetType());
        Assert.Equal(beforeFailure.Message, afterFailure.Message);
    }

    private static void AssertMissing(string parameterName, Action action)
    {
        var failure = Assert.Throws<ArgumentNullException>(action);
        Assert.Equal(parameterName, failure.ParamName);
    }

    private static void AssertFailure(string expectedMessage, Action action)
    {
        var failure = Assert.Throws<ArgumentException>(action);
        Assert.Equal(expectedMessage, failure.Message);
    }

    private delegate string SummaryContract(
        string releaseId,
        IReadOnlyList<DeploymentResult> results);
}
