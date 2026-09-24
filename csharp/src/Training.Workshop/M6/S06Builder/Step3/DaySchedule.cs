using System.Globalization;
using System.Text;

namespace Training.Workshop.M6.S06Builder.Step3;

/// <summary>Krok 3: niemutowalny korzeń - builder jest jedyną wygodną drogą budowy.</summary>
public sealed record DaySchedule(DateOnly Date, IReadOnlyList<Hall> Halls)
{
    public IReadOnlyList<Hall> Halls { get; } = Halls.ToList().AsReadOnly();

    public string Render()
    {
        var text = new StringBuilder()
            .Append(Date.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)).Append(' ').Append(Date.DayOfWeek).Append('\n');
        var count = 0;
        foreach (var hall in Halls)
        {
            text.Append(hall.Render());
            count += hall.Screenings.Count;
        }
        return text.Append("Seansow: ").Append(count).Append('\n').ToString();
    }
}
