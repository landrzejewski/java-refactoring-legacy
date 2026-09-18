using System.Collections.Immutable;

namespace Training.Module8.Collaboration;

public sealed record ChangeSet
{
    public ChangeSet(
        string title,
        IEnumerable<ChangeIntent> intents,
        IEnumerable<VerificationEvidence> verificationEvidence,
        bool independentlyGreenBuild)
    {
        ArgumentNullException.ThrowIfNull(title);
        if (string.IsNullOrWhiteSpace(title))
        {
            throw new ArgumentException("title must not be blank");
        }
        ArgumentNullException.ThrowIfNull(intents);
        ArgumentNullException.ThrowIfNull(verificationEvidence);

        Title = title;
        Intents = intents.ToImmutableHashSet();
        VerificationEvidence = [.. verificationEvidence];
        IndependentlyGreenBuild = independentlyGreenBuild;
    }

    public string Title { get; }

    public IImmutableSet<ChangeIntent> Intents { get; }

    public ImmutableArray<VerificationEvidence> VerificationEvidence { get; }

    public bool IndependentlyGreenBuild { get; }
}
