using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step1;

/// <summary>
/// Krok 1: Replace Inheritance with Delegation dla osi "format". Szkielet deleguje formatowanie
/// do IReportFormat podanego w konstruktorze; w hierarchii zostały tylko haki okresu.
/// </summary>
public abstract class ScreeningReport
{
    private readonly IReportFormat _format;

    protected ScreeningReport(IReportFormat format)
    {
        ArgumentNullException.ThrowIfNull(format);
        _format = format;
    }

    /// <summary>Szkielet raportu - niewirtualny (odpowiednik final).</summary>
    public string Render(DateOnly day, IReadOnlyList<ScreeningSale> sales)
    {
        var byTitle = new SortedDictionary<string, Line>(StringComparer.Ordinal);
        foreach (var sale in sales)
        {
            if (Includes(day, sale.Day))
            {
                var line = new Line(sale.Tickets, sale.Revenue);
                byTitle[sale.Title] = byTitle.TryGetValue(sale.Title, out var existing) ? existing.Plus(line) : line;
            }
        }
        var text = new StringBuilder(_format.Header(Label(day)));
        var total = new Line(0, Money.Zero);
        foreach (var entry in byTitle)
        {
            text.Append(_format.Row(entry.Key, entry.Value.Tickets, entry.Value.Revenue));
            total = total.Plus(entry.Value);
        }
        return text.Append(_format.Footer(total.Tickets, total.Revenue)).ToString();
    }

    /// <summary>Hak okresu: czy sprzedaż z danego dnia należy do raportu.</summary>
    protected abstract bool Includes(DateOnly day, DateOnly saleDay);

    /// <summary>Hak okresu: opis okresu w nagłówku.</summary>
    protected abstract string Label(DateOnly day);

    private sealed record Line(int Tickets, Money Revenue)
    {
        public Line Plus(Line other) => new(Tickets + other.Tickets, Revenue.Plus(other.Revenue));
    }
}
