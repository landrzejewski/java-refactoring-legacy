using System.Collections.Immutable;

namespace Training.Module8.Documentation;

public sealed record DecisionRecord
{
    public DecisionRecord(
        DecisionId id,
        string title,
        DecisionStatus status,
        string context,
        string decision,
        IEnumerable<DecisionOption> consideredOptions,
        IEnumerable<DecisionConsequence> consequences,
        string verificationMethod)
    {
        ArgumentNullException.ThrowIfNull(id);
        Id = id;
        Title = RequireNonBlank(title, nameof(title));
        Status = status;
        Context = RequireNonBlank(context, nameof(context));
        Decision = RequireNonBlank(decision, nameof(decision));
        ConsideredOptions = NonEmptyCopy(
            consideredOptions, nameof(consideredOptions));
        Consequences = NonEmptyCopy(consequences, nameof(consequences));
        VerificationMethod = RequireNonBlank(
            verificationMethod,
            nameof(verificationMethod));
    }

    public DecisionId Id { get; }

    public string Title { get; }

    public DecisionStatus Status { get; }

    public string Context { get; }

    public string Decision { get; }

    public ImmutableArray<DecisionOption> ConsideredOptions { get; }

    public ImmutableArray<DecisionConsequence> Consequences { get; }

    public string VerificationMethod { get; }

    private static string RequireNonBlank(string value, string name)
    {
        ArgumentNullException.ThrowIfNull(value, name);
        if (string.IsNullOrWhiteSpace(value))
        {
            throw new ArgumentException(name + " must not be blank");
        }
        return value;
    }

    private static ImmutableArray<T> NonEmptyCopy<T>(
        IEnumerable<T> values,
        string name)
    {
        ArgumentNullException.ThrowIfNull(values, name);
        ImmutableArray<T> copy = [.. values];
        if (copy.IsEmpty)
        {
            throw new ArgumentException(name + " must not be empty");
        }
        return copy;
    }
}
