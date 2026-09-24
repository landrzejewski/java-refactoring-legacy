using System.Globalization;
using System.Text;

namespace Training.Workshop.M7.S13GodClass.Step1;

/// <summary>
/// Krok 1: Extract Class PricingService - pierwszy pionowy wycinek kampanii.
/// Cennik (ceny formatów, zniżki, poranek, VIP, okulary, rabat grupowy, opłata online)
/// ma teraz jednego właściciela. PricingService nie zna układu object?[] - dostaje wartości.
/// Arytmetyka double przeniesiona dosłownie, więc kwoty są identyczne co do bitu.
/// </summary>
public class CinemaManager
{
    // format: 1 = 2D, 2 = 3D, 3 = IMAX
    // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED
    // typ biletu: N = normalny, S = student, E = senior, C = dziecko

    /// <summary>Hak dla testów dodany "na chwilę" w 2019 roku.</summary>
    internal static Func<DateTime> Clock = () => DateTime.Now;

    private readonly PricingService _pricing = new();

    public void AddScreening(string id, string title, int format,
        DateTime start, int rows, int seatsPerRow, int vipFromRow)
    {
        LegacyDb.Screenings[id] = [title, format, start, rows,
            seatsPerRow, vipFromRow, new HashSet<string>()];
    }

    public string Book(string screeningId, string email, string? phone,
        string[]? seats, string[]? types, bool web, bool ownGlasses)
    {
        LegacyDb.Screenings.TryGetValue(screeningId, out var s);
        if (s != null)
        {
            if (seats != null && seats.Length > 0)
            {
                if (types != null && types.Length == seats.Length)
                {
                    var taken = (HashSet<string>)s[6]!;
                    foreach (var seat in seats)
                    {
                        if (taken.Contains(seat))
                        {
                            return "ERROR: seat taken " + seat;
                        }
                        int row = int.Parse(seat.Substring(1));
                        char letter = seat[0];
                        if (row > (int)s[3]! || letter - 'A' >= (int)s[4]!)
                        {
                            return "ERROR: no such seat " + seat;
                        }
                    }
                    double sum = _pricing.TicketsSum((int)s[1]!, (DateTime)s[2]!, (int)s[5]!,
                        seats, types, ownGlasses);
                    double total = sum + _pricing.BookingFee(web, seats.Length);
                    string id = "B" + (LegacyDb.Sequence++);
                    foreach (var seat in seats)
                    {
                        taken.Add(seat);
                    }
                    LegacyDb.Bookings[id] = [screeningId, email, phone,
                        seats, types, web, total, 0, Clock(), null, sum];
                    LegacyMailer.Send(email, "Rezerwacja " + id,
                        "Film: " + s[0] + ", miejsca: " + string.Join(",", seats)
                        + ", do zaplaty: " + Fmt(total));
                    return id;
                }
                else
                {
                    return "ERROR: types do not match seats";
                }
            }
            else
            {
                return "ERROR: no seats";
            }
        }
        else
        {
            return "ERROR: no screening " + screeningId;
        }
    }

    public string Pay(string bookingId, string? card)
    {
        LegacyDb.Bookings.TryGetValue(bookingId, out var b);
        if (b == null)
        {
            return "ERROR: no booking";
        }
        int status = (int)b[7]!;
        if (status == 1)
        {
            return "ERROR: already paid";
        }
        else if (status == 2)
        {
            return "ERROR: already used";
        }
        else if (status == 3)
        {
            return "ERROR: expired";
        }
        else if (status == 4)
        {
            return "ERROR: cancelled";
        }
        if (!LegacyPaymentGateway.Charge(card, (double)b[6]!))
        {
            LegacyMailer.Send((string)b[1]!, "Platnosc odrzucona", "Rezerwacja " + bookingId);
            return "ERROR: payment declined";
        }
        b[7] = 1;
        b[9] = card;
        string email = (string)b[1]!;
        int points = (int)((double)b[10]! / 10);
        LegacyDb.Loyalty[email] = LegacyDb.Loyalty.GetValueOrDefault(email, 0) + points;
        LegacyMailer.Send(email, "Bilety " + bookingId, "Oplacono " + Fmt((double)b[6]!)
            + ", punkty: +" + points);
        if (b[2] != null)
        {
            LegacyMailer.Sms((string)b[2]!, "CineLegacy: bilety " + bookingId + " oplacone");
        }
        return "OK";
    }

    public string Cancel(string bookingId)
    {
        LegacyDb.Bookings.TryGetValue(bookingId, out var b);
        if (b == null)
        {
            return "ERROR: no booking";
        }
        int status = (int)b[7]!;
        if (status == 2 || status == 3 || status == 4)
        {
            return "ERROR: cannot cancel";
        }
        var s = LegacyDb.Screenings[(string)b[0]!];
        var taken = (HashSet<string>)s[6]!;
        foreach (var seat in (string[])b[3]!)
        {
            taken.Remove(seat);
        }
        b[7] = 4;
        double refund = 0;
        if (status == 1)
        {
            DateTime now = Clock();
            DateTime start = (DateTime)s[2]!;
            double tickets = (double)b[10]!;
            if (now >= start)
            {
                refund = 0;
            }
            else if ((long)(start - now).TotalHours >= 24)
            {
                refund = tickets;
            }
            else
            {
                refund = tickets * 0.5;
            }
            refund = refund - 3.00;
            if (refund < 0)
            {
                refund = 0;
            }
            refund = Math.Floor(refund * 100 + 0.5) / 100.0;
            if (refund > 0)
            {
                LegacyPaymentGateway.Refund((string?)b[9], refund);
            }
            string email = (string)b[1]!;
            int points = (int)(tickets / 10);
            LegacyDb.Loyalty[email] = Math.Max(0, LegacyDb.Loyalty.GetValueOrDefault(email, 0) - points);
        }
        LegacyMailer.Send((string)b[1]!, "Anulowano " + bookingId, "Zwrot: " + Fmt(refund));
        return "REFUND " + Fmt(refund);
    }

    public void ExpireOld()
    {
        DateTime now = Clock();
        foreach (var e in LegacyDb.Bookings)
        {
            var b = e.Value;
            if ((int)b[7]! == 0
                && (long)(now - (DateTime)b[8]!).TotalMinutes >= 15)
            {
                b[7] = 3;
                var s = LegacyDb.Screenings[(string)b[0]!];
                var taken = (HashSet<string>)s[6]!;
                foreach (var seat in (string[])b[3]!)
                {
                    taken.Remove(seat);
                }
                LegacyMailer.Send((string)b[1]!, "Rezerwacja wygasla", e.Key);
            }
        }
    }

    public string Use(string bookingId)
    {
        LegacyDb.Bookings.TryGetValue(bookingId, out var b);
        if (b == null)
        {
            return "ERROR: no booking";
        }
        if ((int)b[7]! != 1)
        {
            return "ERROR: not paid";
        }
        b[7] = 2;
        return "OK";
    }

    public int LoyaltyPoints(string email)
    {
        return LegacyDb.Loyalty.GetValueOrDefault(email, 0);
    }

    public string DailyReport(DateOnly day)
    {
        var sb = new StringBuilder();
        sb.Append("RAPORT DZIENNY ").Append(day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)).Append('\n');
        var byTitle = new SortedDictionary<string, double[]>(StringComparer.Ordinal);
        double fees = 0;
        int tickets = 0;
        foreach (var b in LegacyDb.Bookings.Values)
        {
            int status = (int)b[7]!;
            if (status != 1 && status != 2)
            {
                continue;
            }
            var s = LegacyDb.Screenings[(string)b[0]!];
            if (DateOnly.FromDateTime((DateTime)s[2]!) != day)
            {
                continue;
            }
            if (!byTitle.TryGetValue((string)s[0]!, out var row))
            {
                row = new double[2];
                byTitle[(string)s[0]!] = row;
            }
            row[0] = row[0] + ((string[])b[3]!).Length;
            row[1] = row[1] + (double)b[10]!;
            fees = fees + ((double)b[6]! - (double)b[10]!);
            tickets = tickets + ((string[])b[3]!).Length;
        }
        double revenue = 0;
        foreach (var e in byTitle)
        {
            sb.Append(e.Key).Append(": ").Append((int)e.Value[0])
                .Append(" bil., ").Append(Fmt(e.Value[1])).Append('\n');
            revenue = revenue + e.Value[1];
        }
        sb.Append("Biletow: ").Append(tickets).Append('\n');
        sb.Append("Przychod z biletow: ").Append(Fmt(revenue)).Append('\n');
        sb.Append("Oplaty rezerwacyjne: ").Append(Fmt(fees)).Append('\n');
        sb.Append("Netto (bez VAT 8%): ").Append(Fmt(revenue / 1.08)).Append('\n');
        return sb.ToString();
    }

    public string Settlement(string title, int week)
    {
        double revenue = 0;
        foreach (var b in LegacyDb.Bookings.Values)
        {
            int status = (int)b[7]!;
            var s = LegacyDb.Screenings[(string)b[0]!];
            if ((status == 1 || status == 2) && s[0]!.Equals(title))
            {
                revenue = revenue + (double)b[10]!;
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
            + Fmt(revenue) + ", dla dystrybutora " + Fmt(share);
    }

    public List<string> FreeSeats(string screeningId)
    {
        var s = LegacyDb.Screenings[screeningId];
        var free = new List<string>();
        var taken = (HashSet<string>)s[6]!;
        for (int r = 1; r <= (int)s[3]!; r++)
        {
            for (int c = 0; c < (int)s[4]!; c++)
            {
                string seat = "" + (char)('A' + c) + r;
                if (!taken.Contains(seat))
                {
                    free.Add(seat);
                }
            }
        }
        return free;
    }

    private static string Fmt(double value)
    {
        return value.ToString("F2", CultureInfo.InvariantCulture);
    }
}
