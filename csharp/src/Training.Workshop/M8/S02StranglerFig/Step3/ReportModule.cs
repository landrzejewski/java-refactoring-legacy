using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M8.S02StranglerFig.Step3;

/// <summary>Krok 3: nowy moduł raportów - ten sam format wyjścia, sumy w Money zamiast double.</summary>
public sealed class ReportModule
{
    private readonly BookingLedger _ledger;

    public ReportModule(BookingLedger ledger)
    {
        _ledger = ledger;
    }

    public string Report()
    {
        var byTitle = new SortedDictionary<string, TitleSales>(StringComparer.Ordinal);
        foreach (BookingLedger.Booking booking in _ledger.All())
        {
            var sales = new TitleSales(booking.Tickets, booking.TicketsValue);
            byTitle[booking.Title] = byTitle.TryGetValue(booking.Title, out var existing)
                ? existing.Plus(sales)
                : sales;
        }
        TitleSales total = byTitle.Values.Aggregate(TitleSales.None, (sum, sales) => sum.Plus(sales));
        Money fees = _ledger.All().Select(booking => booking.Fees).Aggregate(Money.Zero, (sum, fee) => sum.Plus(fee));
        var text = new StringBuilder("RAPORT\n");
        foreach (var (title, sales) in byTitle)
        {
            text.Append(title).Append(": ").Append(sales.Tickets)
                .Append(" bil., ").Append(sales.Value).Append('\n');
        }
        return text.Append("Biletow: ").Append(total.Tickets).Append('\n')
            .Append("Przychod z biletow: ").Append(total.Value).Append('\n')
            .Append("Oplaty rezerwacyjne: ").Append(fees).Append('\n')
            .ToString();
    }

    private sealed record TitleSales(int Tickets, Money Value)
    {
        public static readonly TitleSales None = new(0, Money.Zero);

        public TitleSales Plus(TitleSales other)
        {
            return new TitleSales(Tickets + other.Tickets, Value.Plus(other.Value));
        }
    }
}
