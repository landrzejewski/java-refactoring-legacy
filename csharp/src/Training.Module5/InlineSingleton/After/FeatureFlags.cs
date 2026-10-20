using System.Collections.Frozen;

namespace Training.Module5.InlineSingleton.After;

/// <summary>
/// Inline Singleton: zwykły, niezmienny obiekt wartości zamiast globalnej instancji.
/// Tworzony w korzeniu kompozycji i przekazywany przez konstruktor, więc zależność
/// jest jawna, a każdy test może mieć własny zestaw flag bez sprzątania globalnego stanu.
/// </summary>
public sealed record FeatureFlags
{
    public FeatureFlags(IEnumerable<string> enabled)
    {
        ArgumentNullException.ThrowIfNull(enabled);
        Enabled = enabled.ToFrozenSet(StringComparer.Ordinal);
    }

    public IReadOnlySet<string> Enabled { get; }

    public static FeatureFlags Of(params string[] flags) => new(flags);

    /// <summary>
    /// Konfiguracja w postaci <c>"canary,dark-launch"</c>; pusty tekst oznacza brak flag.
    /// </summary>
    public static FeatureFlags Parse(string? config)
    {
        if (string.IsNullOrWhiteSpace(config))
        {
            return Of();
        }
        return new FeatureFlags(config.Split(',')
            .Select(flag => flag.Trim())
            .Where(flag => flag.Length > 0));
    }

    public bool IsEnabled(string flag) => Enabled.Contains(flag);

    // Java Set.equals compares elements; a C# record would compare the set references.
    public bool Equals(FeatureFlags? other) =>
        other is not null && Enabled.SetEquals(other.Enabled);

    public override int GetHashCode() =>
        Enabled.Aggregate(0, (hash, flag) => hash ^ StringComparer.Ordinal.GetHashCode(flag));
}
