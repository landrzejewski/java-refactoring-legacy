using System.Globalization;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step1;

/// <summary>Krok 1: podklasa wybiera format przez konstruktor; zostały w niej tylko haki okresu.</summary>
public sealed class WeeklyCsvReport : ScreeningReport
{
    public WeeklyCsvReport()
        : base(new CsvFormat())
    {
    }

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
}
