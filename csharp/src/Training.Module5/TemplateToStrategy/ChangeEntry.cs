namespace Training.Module5.TemplateToStrategy;

public sealed record ChangeEntry
{
    public ChangeEntry(Kind kind, string? description)
    {
        if (string.IsNullOrWhiteSpace(description))
        {
            throw new ArgumentException("description must not be blank");
        }
        Kind = kind;
        Description = description;
    }

    public Kind Kind { get; }

    public string Description { get; }
}

// Java nests this enum as ChangeEntry.Kind; C# forbids a nested type with the
// same name as the Kind property, so it lives next to the record.
public enum Kind
{
    Feature,
    Fix,
    Breaking
}
