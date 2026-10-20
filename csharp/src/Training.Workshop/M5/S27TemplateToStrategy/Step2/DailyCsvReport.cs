namespace Training.Workshop.M5.S27TemplateToStrategy.Step2;

/// <summary>Krok 2: podklasa to już tylko para strategii - kandydat do usunięcia (Inline Class).</summary>
public sealed class DailyCsvReport : ScreeningReport
{
    public DailyCsvReport()
        : base(new DailyPeriod(), new CsvFormat())
    {
    }
}
