namespace Training.Workshop.M8.S11StagedRollout.Step1;

/// <summary>
/// Krok 1: polityka jako wartość - Introduce Parameter Object dla flagi i listy wyjątków.
/// Konfigurację da się teraz podać z zewnątrz, przetestować i opisać w przeglądzie.
/// </summary>
public sealed record RolloutPolicy(bool Enabled, IReadOnlySet<string> AllowList)
{
    public IReadOnlySet<string> AllowList { get; } = AllowList.ToHashSet();

    /// <summary>Dotychczasowe ustawienia produkcyjne - te same, co stała i if w starym kodzie.</summary>
    public static RolloutPolicy Current()
    {
        return new RolloutPolicy(false, new HashSet<string> { "anna@kino.pl", "jan@kino.pl" });
    }

    public bool Allows(string email)
    {
        return Enabled || AllowList.Contains(email);
    }
}
