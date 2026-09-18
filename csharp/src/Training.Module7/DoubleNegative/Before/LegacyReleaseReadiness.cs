namespace Training.Module7.DoubleNegative.Before;

public sealed record LegacyReleaseReadiness(
    bool NotApproved,
    bool TestsNotPassed,
    bool WindowNotOpen);
