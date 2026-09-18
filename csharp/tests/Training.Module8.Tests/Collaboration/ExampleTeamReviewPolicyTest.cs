using Training.Module8.Collaboration;

namespace Training.Module8.Tests.Collaboration;

public sealed class ExampleTeamReviewPolicyTest
{
    private readonly ExampleTeamReviewPolicy policy = new();

    [Fact]
    public void AcceptsAFocusedChangeWithEvidenceAndAGreenBuild()
    {
        var changeSet = new ChangeSet(
            "Extract deployment clock",
            [ChangeIntent.Refactoring],
            [new VerificationEvidence(
                EvidenceKind.AutomatedTest,
                "dotnet test: 42 tests passed")],
            true);

        ReviewReadiness readiness = policy.Assess(changeSet);

        Assert.True(readiness.Ready);
        Assert.Empty(readiness.Problems);
    }

    [Fact]
    public void ReportsMixedIntentMissingEvidenceAndNonGreenBuildInStableOrder()
    {
        var changeSet = new ChangeSet(
            "Move validator and change its rules",
            [ChangeIntent.Refactoring, ChangeIntent.BehaviorChange],
            [],
            false);

        ReviewReadiness readiness = policy.Assess(changeSet);

        Assert.False(readiness.Ready);
        Assert.Equal<ReadinessProblem>(
            [
                ReadinessProblem.MixedPrimaryIntents,
                ReadinessProblem.MissingVerificationEvidence,
                ReadinessProblem.BuildNotIndependentlyGreen
            ],
            readiness.Problems);
    }

    [Fact]
    public void ReportsAnUnspecifiedIntentAsATypedProblem()
    {
        var changeSet = new ChangeSet(
            "Unclassified change",
            [],
            [new VerificationEvidence(
                EvidenceKind.StaticAnalysis,
                "No new findings")],
            true);

        Assert.Equal<ReadinessProblem>(
            [ReadinessProblem.MissingIntent],
            policy.Assess(changeSet).Problems);
    }

    [Fact]
    public void SnapshotsMutableInputCollections()
    {
        var intents = new HashSet<ChangeIntent> { ChangeIntent.CharacterizationTests };
        var evidence = new List<VerificationEvidence>
        {
            new(EvidenceKind.AutomatedTest, "Characterization suite passed")
        };

        var changeSet = new ChangeSet("Capture behavior", intents, evidence, true);
        intents.Add(ChangeIntent.Rollout);
        evidence.Clear();

        Assert.True(changeSet.Intents.SetEquals(
            [ChangeIntent.CharacterizationTests]));
        Assert.Single(changeSet.VerificationEvidence);
        Assert.Throws<NotSupportedException>(
            () => ((ICollection<ChangeIntent>)changeSet.Intents)
                .Add(ChangeIntent.Rollout));
        Assert.Throws<NotSupportedException>(
            () => ((ICollection<VerificationEvidence>)changeSet.VerificationEvidence)
                .Clear());
    }
}
