using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step2;

/// <summary>Krok 2: bez zmian - format tekstowy w jednym miejscu (był skopiowany w DailyTextReport i WeeklyTextReport).</summary>
public sealed class TextFormat : IReportFormat
{
    public string Header(string label)
    {
        return "RAPORT " + label.ToUpperInvariant() + "\n" + new string('-', 37) + "\n";
    }

    public string Row(string title, int tickets, Money revenue)
    {
        return string.Format(CultureInfo.InvariantCulture, "{0,-20} {1,5} {2,10}", title, tickets, revenue) + "\n";
    }

    public string Footer(int tickets, Money revenue)
    {
        return new string('-', 37) + "\n" + Row("RAZEM", tickets, revenue);
    }
}
