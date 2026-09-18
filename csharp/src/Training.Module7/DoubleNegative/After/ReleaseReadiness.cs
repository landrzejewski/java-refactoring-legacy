namespace Training.Module7.DoubleNegative.After;

public sealed record ReleaseReadiness(
    bool Approved,
    bool TestsPassed,
    bool WindowOpen);
