using Training.Module7.Arrowhead;
using Training.Module7.Arrowhead.After;
using Training.Module7.Arrowhead.Before;

namespace Training.Module7.Tests;

public sealed class ArrowheadEquivalenceTest
{
    private readonly LegacyDeploymentEligibility _before = new();
    private readonly DeploymentEligibility _after = new();

    [Fact]
    public void GuardClausesPreserveTheCompleteDecisionMatrix()
    {
        bool[] values = [false, true];

        foreach (var approved in values)
        {
            foreach (var testsPassed in values)
            {
                foreach (var windowOpen in values)
                {
                    var candidate = new DeploymentCandidate(
                        "rel-42", approved, testsPassed, windowOpen);
                    var expected = !approved
                        ? Eligibility.NotApproved
                        : !testsPassed
                            ? Eligibility.TestsFailed
                            : !windowOpen
                                ? Eligibility.WindowClosed
                                : Eligibility.Eligible;

                    Assert.Equal(expected, _before.Evaluate(candidate));
                    Assert.Equal(expected, _after.Evaluate(candidate));
                }
            }
        }
    }

    [Fact]
    public void GuardClausesPreserveValidationAndFailurePriority()
    {
        AssertSameDecision(null, Eligibility.MissingCandidate);
        AssertSameDecision(
            new DeploymentCandidate(null, false, false, false),
            Eligibility.InvalidReleaseId);
        AssertSameDecision(
            new DeploymentCandidate(" \t", false, false, false),
            Eligibility.InvalidReleaseId);
        AssertSameDecision(
            new DeploymentCandidate("rel-42", false, false, false),
            Eligibility.NotApproved);
        AssertSameDecision(
            new DeploymentCandidate("rel-42", true, false, false),
            Eligibility.TestsFailed);
        AssertSameDecision(
            new DeploymentCandidate("rel-42", true, true, false),
            Eligibility.WindowClosed);
    }

    private void AssertSameDecision(DeploymentCandidate? candidate, Eligibility expected)
    {
        Assert.Equal(expected, _before.Evaluate(candidate));
        Assert.Equal(expected, _after.Evaluate(candidate));
        Assert.Equal(_before.Evaluate(candidate), _after.Evaluate(candidate));
    }
}
