namespace Training.Module5.AntiCorruptionLayer.After;

/// <summary>
/// Model domenowy środowiska - nazwany w języku planowania wdrożeń, nie CMDB.
/// </summary>
public sealed record Environment
{
    public Environment(string? name, Tier tier, bool active, DateOnly lastDeployment)
    {
        if (string.IsNullOrWhiteSpace(name))
        {
            throw new ArgumentException("name must not be blank");
        }
        Name = name;
        Tier = tier;
        Active = active;
        LastDeployment = lastDeployment;
    }

    public string Name { get; }

    public Tier Tier { get; }

    public bool Active { get; }

    public DateOnly LastDeployment { get; }
}

// Java nests this enum as Environment.Tier; C# forbids a nested type with the
// same name as the Tier property, so it lives next to the record.
public enum Tier
{
    Production,
    Test,
    Development
}
