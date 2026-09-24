using System.Globalization;

namespace Training.Workshop.M6.S06Builder.Step2;

/// <summary>Krok 2: liść jako rekord.</summary>
public sealed record Screening(string Title, TimeOnly Start)
{
    public string Render()
    {
        return "  " + Start.ToString("HH:mm", CultureInfo.InvariantCulture) + " " + Title + "\n";
    }
}
