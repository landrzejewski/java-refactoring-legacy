namespace Training.Workshop.M3.S12CleanArchitecture.Step4.App;

/// <summary>Krok 1: dane wejściowe przypadku użycia - prosty rekord, bez słownika z HTTP.</summary>
public sealed record BookSeatsCommand(string Email, string Format, IReadOnlyList<int> Rows)
{
    public IReadOnlyList<int> Rows { get; } = Rows.ToList().AsReadOnly();
}
