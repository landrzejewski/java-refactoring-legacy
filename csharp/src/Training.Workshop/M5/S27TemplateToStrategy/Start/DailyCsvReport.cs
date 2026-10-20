using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Start;

/// <summary>Start: okres dzienny + format CSV.</summary>
public sealed class DailyCsvReport : ScreeningReport
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
