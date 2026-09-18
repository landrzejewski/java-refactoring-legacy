using System.Collections.Immutable;

namespace Training.Module8.Collaboration;

public sealed record ReviewReadiness
{
    public ReviewReadiness(IEnumerable<ReadinessProblem> problems)
    {
        ArgumentNullException.ThrowIfNull(problems);
        Problems = [.. problems];
    }

    public ImmutableArray<ReadinessProblem> Problems { get; }

    public bool Ready => Problems.IsEmpty;
}
