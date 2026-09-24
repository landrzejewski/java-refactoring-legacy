using System.Globalization;
using System.Text;

namespace Training.Workshop.M7.S13GodClass.Step4;

/// <summary>
/// Krok 4: raporty mają jednego właściciela. Czytają rezerwacje przez BookingRepository
/// (nie przez object?[]), więc kolejny krok może zmienić magazyn bez dotykania raportów.
/// Kod metod przeniesiony dosłownie - kolejność sumowania double bez zmian.
/// </summary>
internal sealed class ReportService
{
    private readonly BookingRepository _bookings;

    internal ReportService(BookingRepository bookings)
    {
        ArgumentNullException.ThrowIfNull(bookings);
        _bookings = bookings;
    }

    internal string DailyReport(DateOnly day)
    {
        var sb = new StringBuilder();
        sb.Append("RAPORT DZIENNY ").Append(day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)).Append('\n');
        var byTitle = new SortedDictionary<string, double[]>(StringComparer.Ordinal);
        double fees = 0;
        int tickets = 0;
        foreach (var b in _bookings.All())
        {
            int status = b.Status;
            if (status != 1 && status != 2)
            {
                continue;
            }
            var s = LegacyDb.Screenings[b.ScreeningId];
            if (DateOnly.FromDateTime((DateTime)s[2]!) != day)
            {
                continue;
            }
            if (!byTitle.TryGetValue((string)s[0]!, out var row))
            {
                row = new double[2];
                byTitle[(string)s[0]!] = row;
            }
            row[0] = row[0] + b.Seats.Length;
            row[1] = row[1] + b.TicketsSum;
            fees = fees + (b.Total - b.TicketsSum);
            tickets = tickets + b.Seats.Length;
        }
        double revenue = 0;
        foreach (var e in byTitle)
        {
            sb.Append(e.Key).Append(": ").Append((int)e.Value[0])
                .Append(" bil., ").Append(Formats.Amount(e.Value[1])).Append('\n');
            revenue = revenue + e.Value[1];
        }
        sb.Append("Biletow: ").Append(tickets).Append('\n');
        sb.Append("Przychod z biletow: ").Append(Formats.Amount(revenue)).Append('\n');
        sb.Append("Oplaty rezerwacyjne: ").Append(Formats.Amount(fees)).Append('\n');
        sb.Append("Netto (bez VAT 8%): ").Append(Formats.Amount(revenue / 1.08)).Append('\n');
        return sb.ToString();
    }

    internal string Settlement(string title, int week)
    {
        double revenue = 0;
        foreach (var b in _bookings.All())
        {
            int status = b.Status;
            var s = LegacyDb.Screenings[b.ScreeningId];
            if ((status == 1 || status == 2) && s[0]!.Equals(title))
            {
                revenue = revenue + b.TicketsSum;
            }
        }
        double share;
        if (week == 1)
        {
            share = revenue * 0.50;
        }
        else if (week == 2)
        {
            share = revenue * 0.40;
        }
        else
        {
            share = revenue * 0.35;
        }
        if (share < 500.00)
        {
            share = 500.00;
        }
        return "ROZLICZENIE " + title + " tydzien " + week + ": przychod "
            + Formats.Amount(revenue) + ", dla dystrybutora " + Formats.Amount(share);
    }
}
