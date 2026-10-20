using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step3;

/// <summary>
/// Krok 3: jedna zapieczętowana klasa złożona z dwóch strategii (Inline Class dla czterech podklas).
/// Template Method był dobry, dopóki zmieniała się jedna oś; przy dwóch niezależnych osiach
/// dziedziczenie mnoży klasy (2 x 2), a kompozycja je dodaje (2 + 2) i pozwala wybrać
/// kombinację w czasie działania. Kolejność kroków raportu nadal pilnuje jedna metoda Render.
/// </summary>
public sealed class ScreeningReport
{
    private readonly IReportPeriod _period;
    private readonly IReportFormat _format;

    public ScreeningReport(IReportPeriod period, IReportFormat format)
    {
        ArgumentNullException.ThrowIfNull(period);
        ArgumentNullException.ThrowIfNull(format);
        _period = period;
        _format = format;
    }

    public string Render(DateOnly day, IReadOnlyList<ScreeningSale> sales)
    {
        var byTitle = new SortedDictionary<string, Line>(StringComparer.Ordinal);
        foreach (var sale in sales)
        {
            if (_period.Includes(day, sale.Day))
            {
                var line = new Line(sale.Tickets, sale.Revenue);
                byTitle[sale.Title] = byTitle.TryGetValue(sale.Title, out var existing) ? existing.Plus(line) : line;
            }
        }
        var text = new StringBuilder(_format.Header(_period.Label(day)));
        var total = new Line(0, Money.Zero);
        foreach (var entry in byTitle)
        {
            text.Append(_format.Row(entry.Key, entry.Value.Tickets, entry.Value.Revenue));
            total = total.Plus(entry.Value);
        }
        return text.Append(_format.Footer(total.Tickets, total.Revenue)).ToString();
    }

    private sealed record Line(int Tickets, Money Revenue)
    {
        public Line Plus(Line other) => new(Tickets + other.Tickets, Revenue.Plus(other.Revenue));
    }
}
