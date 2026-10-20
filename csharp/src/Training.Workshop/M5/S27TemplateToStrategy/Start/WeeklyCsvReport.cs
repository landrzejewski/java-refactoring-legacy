using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Start;

/// <summary>Start: okres tygodniowy (poniedziałek-niedziela) + format CSV skopiowany z DailyCsvReport.</summary>
public sealed class WeeklyCsvReport : ScreeningReport
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
        return "# raport " + label + "\nfilm;bilety;przychod\n";
    }

    protected override string Row(string title, int tickets, Money revenue)
    {
        var cell = title.Contains(';') ? "\"" + title + "\"" : title;
        return cell + ";" + tickets + ";" + revenue + "\n";
    }

    protected override string Footer(int tickets, Money revenue)
    {
        return "SUMA;" + tickets + ";" + revenue + "\n";
    }
}
