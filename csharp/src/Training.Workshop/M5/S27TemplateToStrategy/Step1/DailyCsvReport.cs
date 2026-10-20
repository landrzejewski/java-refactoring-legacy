using System.Globalization;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step1;

/// <summary>Krok 1: podklasa wybiera format przez konstruktor; zostały w niej tylko haki okresu.</summary>
public sealed class DailyCsvReport : ScreeningReport
{
    public DailyCsvReport()
        : base(new CsvFormat())
    {
    }

    protected override bool Includes(DateOnly day, DateOnly saleDay)
    {
        return saleDay == day;
    }

    protected override string Label(DateOnly day)
    {
        return "dzien " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    }
}
