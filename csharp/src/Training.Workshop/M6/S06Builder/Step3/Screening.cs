using System.Globalization;

namespace Training.Workshop.M6.S06Builder.Step3;

/// <summary>Krok 3: liść jako rekord.</summary>
public sealed record Screening(string Title, TimeOnly Start)
{
    public string Render()
    {
        return "  " + Start.ToString("HH:mm", CultureInfo.InvariantCulture) + " " + Title + "\n";
    }
}
