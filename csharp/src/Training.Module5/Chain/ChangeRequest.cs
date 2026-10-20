namespace Training.Module5.Chain;

public sealed record ChangeRequest
{
    public ChangeRequest(string? id, int linesChanged, Risk risk, bool production)
    {
        if (string.IsNullOrWhiteSpace(id))
        {
            throw new ArgumentException("id must not be blank");
        }
        if (linesChanged < 0)
        {
            throw new ArgumentException("linesChanged must not be negative");
        }
        Id = id;
        LinesChanged = linesChanged;
        Risk = risk;
        Production = production;
    }

    public string Id { get; }

    public int LinesChanged { get; }

    public Risk Risk { get; }

    public bool Production { get; }
}

// Java nests this enum as ChangeRequest.Risk; C# forbids a nested type with the
// same name as the Risk property, so it lives next to the record.
public enum Risk
{
    Low,
    Medium,
    High
}
