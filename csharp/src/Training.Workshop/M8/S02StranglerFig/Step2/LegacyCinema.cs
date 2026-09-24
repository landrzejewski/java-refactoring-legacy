using System.Globalization;
using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M8.S02StranglerFig.Step2;

/// <summary>
/// Krok 2 (bez zmian): stary system. Od kroku 1 klienci widzą go tylko przez CinemaFacade.
/// </summary>
public sealed class LegacyCinema : ICinemaApi
{
    private readonly BookingLedger _ledger;

    public LegacyCinema(BookingLedger ledger)
    {
        _ledger = ledger;
    }

    public string Book(string email, string title, int format, int tickets, bool web)
    {
        if (tickets <= 0)
        {
            return "ERROR: no seats";
        }
        double p = format == 1 ? 25.00 : format == 2 ? 32.00 : 40.00;
        double sum = p * tickets;
        if (tickets >= 10)
        {
            sum = sum - sum * 0.10;
        }
        double fees = web ? 2.00 * tickets : 0;
        string id = _ledger.NextId();
        _ledger.Add(new BookingLedger.Booking(id, email, title, tickets,
            new Money((decimal)sum), new Money((decimal)fees)));
        return id;
    }

    public string Report()
    {
        var byTitle = new SortedDictionary<string, double[]>(StringComparer.Ordinal);
        double fees = 0;
        int count = 0;
        foreach (BookingLedger.Booking b in _ledger.All())
        {
            if (!byTitle.TryGetValue(b.Title, out double[]? row))
            {
                row = new double[2];
                byTitle[b.Title] = row;
            }
            row[0] = row[0] + b.Tickets;
            row[1] = row[1] + (double)b.TicketsValue.Amount;
            fees = fees + (double)b.Fees.Amount;
            count = count + b.Tickets;
        }
        var sb = new StringBuilder("RAPORT\n");
        double revenue = 0;
        foreach (KeyValuePair<string, double[]> e in byTitle)
        {
            sb.Append(e.Key).Append(": ").Append((int)e.Value[0]).Append(" bil., ")
                .Append(e.Value[1].ToString("F2", CultureInfo.InvariantCulture)).Append('\n');
            revenue = revenue + e.Value[1];
        }
        sb.Append("Biletow: ").Append(count).Append('\n');
        sb.Append("Przychod z biletow: ").Append(revenue.ToString("F2", CultureInfo.InvariantCulture)).Append('\n');
        sb.Append("Oplaty rezerwacyjne: ").Append(fees.ToString("F2", CultureInfo.InvariantCulture)).Append('\n');
        return sb.ToString();
    }
}
