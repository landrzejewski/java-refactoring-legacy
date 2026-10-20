namespace Training.Workshop.M5.S27TemplateToStrategy.Step2;

/// <summary>
/// Stabilny punkt wejścia sceny: raport dla nazwy okresu (DAILY, WEEKLY) i formatu (CSV, TEXT).
/// Krok 2: bez zmian - cztery podklasy są już tylko konfiguracją dwóch strategii.
/// </summary>
public static class Reports
{
    public static ScreeningReport Of(string period, string format)
    {
        return period switch
        {
            "DAILY" => format switch
            {
                "CSV" => new DailyCsvReport(),
                "TEXT" => new DailyTextReport(),
                _ => throw UnknownFormat(format),
            },
            "WEEKLY" => format switch
            {
                "CSV" => new WeeklyCsvReport(),
                "TEXT" => new WeeklyTextReport(),
                _ => throw UnknownFormat(format),
            },
            _ => throw new ArgumentException("unknown period: " + period),
        };
    }

    private static ArgumentException UnknownFormat(string format)
    {
        return new ArgumentException("unknown format: " + format);
    }
}
