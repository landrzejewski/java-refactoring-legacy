using System.Text;

namespace Training.Workshop.M6.S06Builder.Step2;

/// <summary>Krok 2: niemutowalny węzeł - lista kopiowana w konstruktorze, bez Add.</summary>
public sealed record Hall(string Name, IReadOnlyList<Screening> Screenings)
{
    public IReadOnlyList<Screening> Screenings { get; } = Screenings.ToList().AsReadOnly();

    public string Render()
    {
        var text = new StringBuilder(Name).Append('\n');
        if (Screenings.Count == 0)
        {
            text.Append("  (brak seansow)\n");
        }
        foreach (var screening in Screenings)
        {
            text.Append(screening.Render());
        }
        return text.ToString();
    }
}
