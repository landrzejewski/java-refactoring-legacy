using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step1;

/// <summary>Krok 1: format CSV w jednym miejscu (był skopiowany w DailyCsvReport i WeeklyCsvReport).</summary>
public sealed class CsvFormat : IReportFormat
{
    public string Header(string label)
    {
        return "# raport " + label + "\nfilm;bilety;przychod\n";
    }

    public string Row(string title, int tickets, Money revenue)
    {
        var cell = title.Contains(';') ? "\"" + title + "\"" : title;
        return cell + ";" + tickets + ";" + revenue + "\n";
    }

    public string Footer(int tickets, Money revenue)
    {
        return "SUMA;" + tickets + ";" + revenue + "\n";
    }
}
