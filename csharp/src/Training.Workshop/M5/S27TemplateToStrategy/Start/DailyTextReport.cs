using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Start;

/// <summary>Start: okres dzienny skopiowany z DailyCsvReport + format tekstowy z kolumnami.</summary>
public sealed class DailyTextReport : ScreeningReport
{
    protected override bool Includes(DateOnly day, DateOnly saleDay)
    {
        return saleDay == day;
    }

    protected override string Label(DateOnly day)
    {
        return "dzien " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
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
