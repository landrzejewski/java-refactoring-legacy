using System.Globalization;
using System.Text;

namespace Training.Workshop.M6.S06Builder.Step1;

/// <summary>Krok 1: bez zmian - korzeń drzewa: dzień z listą sal.</summary>
public sealed class DaySchedule
{
    private readonly DateOnly _date;
    private readonly List<Hall> _halls = [];

    public DaySchedule(DateOnly date)
    {
        _date = date;
    }

    public void Add(Hall hall)
    {
        _halls.Add(hall);
    }

    public string Render()
    {
        var text = new StringBuilder()
            .Append(_date.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)).Append(' ').Append(_date.DayOfWeek).Append('\n');
        var count = 0;
        foreach (var hall in _halls)
        {
            text.Append(hall.Render());
            count += hall.Size();
        }
        return text.Append("Seansow: ").Append(count).Append('\n').ToString();
    }
}
