using System.Globalization;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step2;

/// <summary>Krok 2: tydzień poniedziałek-niedziela w jednym miejscu (był skopiowany w obu Weekly*Report).</summary>
public sealed class WeeklyPeriod : IReportPeriod
{
    public bool Includes(DateOnly day, DateOnly saleDay)
    {
        var monday = Monday(day);
        return saleDay >= monday && saleDay <= monday.AddDays(6);
    }

    public string Label(DateOnly day)
    {
        var monday = Monday(day);
        return "tydzien " + monday.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
            + ".." + monday.AddDays(6).ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    }

    /// <summary>Poniedziałek tego samego tygodnia (DayOfWeek liczy od niedzieli = 0).</summary>
    private static DateOnly Monday(DateOnly day)
    {
        return day.AddDays(-(((int)day.DayOfWeek + 6) % 7));
    }
}
