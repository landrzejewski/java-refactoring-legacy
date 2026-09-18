namespace Training.Module7.MethodObject;

public sealed record RiskAssessment
{
    public RiskAssessment(int score, RiskLevel level)
    {
        if (score is < 0 or > 100)
        {
            throw new ArgumentException("score must be between 0 and 100");
        }
        // C# enums cannot be null; the equivalent of Java's requireNonNull
        // is rejecting values outside the declared members.
        if (!Enum.IsDefined(level))
        {
            throw new ArgumentOutOfRangeException(nameof(level), level, "level must be a defined RiskLevel");
        }
        Score = score;
        Level = level;
    }

    public int Score { get; }

    public RiskLevel Level { get; }
}
