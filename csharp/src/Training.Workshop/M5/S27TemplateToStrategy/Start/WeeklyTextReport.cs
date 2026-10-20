using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Start;

/// <summary>Start: okres tygodniowy skopiowany z WeeklyCsvReport + format tekstowy skopiowany z DailyTextReport.</summary>
public sealed class WeeklyTextReport : ScreeningReport
{
    protected override bool Includes(DateOnly day, DateOnly saleDay)
    {
        var monday = day.AddDays(-(((int)day.DayOfWeek + 6) % 7));
        return saleDay >= monday && saleDay <= monday.AddDays(6);
    }

    protected override string Label(DateOnly day)
    {
        var monday = day.AddDays(-(((int)day.DayOfWeek + 6) % 7));
        return "tydzien " + monday.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
            + ".." + monday.AddDays(6).ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    }

    protected override string Header(string label)
    {
        return "RAPORT " + label.ToUpperInvariant() + "\n" + new string('-', 37) + "\n";
    }

    protected override string Row(string title, int tickets, Money revenue)
    {
        return string.Format(CultureInfo.InvariantCulture, "{0,-20} {1,5} {2,10}", title, tickets, revenue) + "\n";
    }

    protected override string Footer(int tickets, Money revenue)
    {
        return new string('-', 37) + "\n" + Row("RAZEM", tickets, revenue);
    }
}
