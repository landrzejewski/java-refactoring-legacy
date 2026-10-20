namespace Training.Workshop.M5.S27TemplateToStrategy.Step2;

/// <summary>Krok 2: podklasa to już tylko para strategii - kandydat do usunięcia (Inline Class).</summary>
public sealed class DailyTextReport : ScreeningReport
{
    public DailyTextReport()
        : base(new DailyPeriod(), new TextFormat())
    {
    }
}
