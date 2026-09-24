using System.Globalization;

namespace Training.Workshop.M6.S06Builder.Start;

/// <summary>Start - liść drzewa repertuaru.</summary>
public sealed class Screening
{
    private readonly string _title;
    private readonly TimeOnly _start;

    public Screening(string title, TimeOnly start)
    {
        _title = title;
        _start = start;
    }

    public string Render()
    {
        return "  " + _start.ToString("HH:mm", CultureInfo.InvariantCulture) + " " + _title + "\n";
    }
}
