namespace Training.Module7.DoubleNegative.Before;

public sealed class LegacyReleaseGate
{
    public bool CanRelease(LegacyReleaseReadiness readiness)
    {
        ArgumentNullException.ThrowIfNull(readiness);
        return !readiness.NotApproved
            && !readiness.TestsNotPassed
            && !readiness.WindowNotOpen;
    }
}
