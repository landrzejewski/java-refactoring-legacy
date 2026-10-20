using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Start;

/// <summary>
/// Start: Template Method z dwiema niezależnymi osiami w hakach - okres (które dni, jaki opis)
/// i format (CSV albo tekst). Każda kombinacja to osobna podklasa: 2 okresy x 2 formaty = 4 klasy,
/// a trzeci format dopisze kolejne dwie. Kod formatu i okresu jest skopiowany między podklasami.
/// </summary>
public abstract class ScreeningReport
{
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
        var text = new StringBuilder(Header(Label(day)));
        var total = new Line(0, Money.Zero);
        foreach (var entry in byTitle)
        {
            text.Append(Row(entry.Key, entry.Value.Tickets, entry.Value.Revenue));
            total = total.Plus(entry.Value);
        }
        return text.Append(Footer(total.Tickets, total.Revenue)).ToString();
    }

    /// <summary>Hak okresu: czy sprzedaż z danego dnia należy do raportu.</summary>
    protected abstract bool Includes(DateOnly day, DateOnly saleDay);

    /// <summary>Hak okresu: opis okresu w nagłówku.</summary>
    protected abstract string Label(DateOnly day);

    /// <summary>Hak formatu.</summary>
    protected abstract string Header(string label);

    /// <summary>Hak formatu.</summary>
    protected abstract string Row(string title, int tickets, Money revenue);

    /// <summary>Hak formatu.</summary>
    protected abstract string Footer(int tickets, Money revenue);

    private sealed record Line(int Tickets, Money Revenue)
    {
        public Line Plus(Line other) => new(Tickets + other.Tickets, Revenue.Plus(other.Revenue));
    }
}
