using System.Globalization;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step2;

/// <summary>Krok 2: okres dzienny w jednym miejscu (był skopiowany w DailyCsvReport i DailyTextReport).</summary>
public sealed class DailyPeriod : IReportPeriod
{
    public bool Includes(DateOnly day, DateOnly saleDay)
    {
        return saleDay == day;
    }

    public string Label(DateOnly day)
    {
        return "dzien " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    }
}
