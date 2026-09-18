namespace Training.Module8.Documentation;

public sealed record DecisionConsequence
{
    public DecisionConsequence(ConsequenceKind kind, string description)
    {
        ArgumentNullException.ThrowIfNull(description);
        if (string.IsNullOrWhiteSpace(description))
        {
            throw new ArgumentException("description must not be blank");
        }
        Kind = kind;
        Description = description;
    }

    public ConsequenceKind Kind { get; }

    public string Description { get; }
}
