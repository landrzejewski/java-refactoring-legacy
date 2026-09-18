namespace Training.Module8.Documentation;

public sealed record DecisionOption
{
    public DecisionOption(string name, string rationale)
    {
        Name = RequireNonBlank(name, nameof(name));
        Rationale = RequireNonBlank(rationale, nameof(rationale));
    }

    public string Name { get; }

    public string Rationale { get; }

    private static string RequireNonBlank(string value, string name)
    {
        ArgumentNullException.ThrowIfNull(value, name);
        if (string.IsNullOrWhiteSpace(value))
        {
            throw new ArgumentException(name + " must not be blank");
        }
        return value;
    }
}
