namespace Training.Module7.DoubleNegative.After;

public sealed class ReleaseGate
{
    public bool CanRelease(ReleaseReadiness readiness)
    {
        ArgumentNullException.ThrowIfNull(readiness);
        return readiness.Approved
            && readiness.TestsPassed
            && readiness.WindowOpen;
    }
}
