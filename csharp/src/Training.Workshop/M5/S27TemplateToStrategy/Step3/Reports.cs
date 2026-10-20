namespace Training.Workshop.M5.S27TemplateToStrategy.Step3;

/// <summary>
/// Stabilny punkt wejścia sceny. Krok 3: osie wybierane niezależnie - dwie mapy zamiast
/// zagnieżdżonego switch. Kolejność walidacji (najpierw okres, potem format) jak w Start.
/// </summary>
public static class Reports
{
    private static readonly IReadOnlyDictionary<string, IReportPeriod> Periods = new Dictionary<string, IReportPeriod>
    {
        ["DAILY"] = new DailyPeriod(),
        ["WEEKLY"] = new WeeklyPeriod(),
    };

    private static readonly IReadOnlyDictionary<string, IReportFormat> Formats = new Dictionary<string, IReportFormat>
    {
        ["CSV"] = new CsvFormat(),
        ["TEXT"] = new TextFormat(),
    };

    public static ScreeningReport Of(string period, string format)
    {
        if (!Periods.TryGetValue(period, out var chosenPeriod))
        {
            throw new ArgumentException("unknown period: " + period);
        }
        if (!Formats.TryGetValue(format, out var chosenFormat))
        {
            throw new ArgumentException("unknown format: " + format);
        }
        return new ScreeningReport(chosenPeriod, chosenFormat);
    }
}
