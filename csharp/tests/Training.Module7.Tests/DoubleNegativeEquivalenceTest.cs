using Training.Module7.DoubleNegative.After;
using Training.Module7.DoubleNegative.Before;
using static Training.Module7.Tests.FailureAssertions;

namespace Training.Module7.Tests;

public sealed class DoubleNegativeEquivalenceTest
{
    [Fact]
    public void PositiveNamesPreserveAllEightTruthTableRows()
    {
        var before = new LegacyReleaseGate();
        var after = new ReleaseGate();
        bool[] values = [false, true];

        foreach (var notApproved in values)
        {
            foreach (var testsNotPassed in values)
            {
                foreach (var windowNotOpen in values)
                {
                    var legacyResult = before.CanRelease(
                        new LegacyReleaseReadiness(notApproved, testsNotPassed, windowNotOpen));
                    var refactoredResult = after.CanRelease(
                        new ReleaseReadiness(!notApproved, !testsNotPassed, !windowNotOpen));

                    Assert.Equal(legacyResult, refactoredResult);
                    Assert.Equal(
                        !notApproved && !testsNotPassed && !windowNotOpen,
                        refactoredResult);
                }
            }
        }
    }

    [Fact]
    public void BothGatesRejectMissingReadinessWithTheSameContract()
    {
        AssertSameFailure(
            () => new LegacyReleaseGate().CanRelease(null!),
            () => new ReleaseGate().CanRelease(null!));
    }
}
