namespace Training.Workshop.M7.S13GodClass.Step4;

/// <summary>
/// Krok 4 (rozwiązanie): Extract Class ReportService - raport dzienny i rozliczenie z dystrybutorem
/// przeniesione (Move Function) do usługi czytającej z BookingRepository. CinemaManager
/// zostaje fasadą o niezmienionym publicznym API. Dalsze wycinki (zwroty, lojalność, repertuar)
/// robimy wtedy, gdy przyniosą wartość dla planowanej zmiany - kampania, nie przepisanie.
/// Krok 1: PricingService, krok 2: NotificationService, krok 3: BookingRepository.
/// </summary>
public class CinemaManager
{
    // format: 1 = 2D, 2 = 3D, 3 = IMAX
    // status: 0 = NEW, 1 = PAID, 2 = USED, 3 = EXPIRED, 4 = CANCELLED
    // typ biletu: N = normalny, S = student, E = senior, C = dziecko

    /// <summary>Hak dla testów dodany "na chwilę" w 2019 roku.</summary>
    internal static Func<DateTime> Clock = () => DateTime.Now;

    private readonly PricingService _pricing = new();
    private readonly NotificationService _notifications = new();
    private readonly BookingRepository _bookings = new();
    private readonly ReportService _reports;

    public CinemaManager()
    {
        _reports = new ReportService(_bookings);
    }

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
                    string id = _bookings.NextId();
                    foreach (var seat in seats)
                    {
                        taken.Add(seat);
                    }
                    _bookings.Save(new Booking(id, screeningId, email, phone, seats, types, web,
                        total, Clock(), sum));
                    _notifications.BookingCreated(email, id, (string)s[0]!, seats, total);
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
        var b = _bookings.Find(bookingId);
        if (b == null)
        {
            return "ERROR: no booking";
        }
        int status = b.Status;
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
        if (!LegacyPaymentGateway.Charge(card, b.Total))
        {
            _notifications.PaymentDeclined(b.Email, bookingId);
            return "ERROR: payment declined";
        }
        b.MarkPaid(card);
        string email = b.Email;
        int points = (int)(b.TicketsSum / 10);
        LegacyDb.Loyalty[email] = LegacyDb.Loyalty.GetValueOrDefault(email, 0) + points;
        _notifications.TicketsPaid(email, b.Phone, bookingId, b.Total, points);
        return "OK";
    }

    public string Cancel(string bookingId)
    {
        var b = _bookings.Find(bookingId);
        if (b == null)
        {
            return "ERROR: no booking";
        }
        int status = b.Status;
        if (status == 2 || status == 3 || status == 4)
        {
            return "ERROR: cannot cancel";
        }
        var s = LegacyDb.Screenings[b.ScreeningId];
        var taken = (HashSet<string>)s[6]!;
        foreach (var seat in b.Seats)
        {
            taken.Remove(seat);
        }
        b.Status = 4;
        double refund = 0;
        if (status == 1)
        {
            DateTime now = Clock();
            DateTime start = (DateTime)s[2]!;
            double tickets = b.TicketsSum;
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
                LegacyPaymentGateway.Refund(b.Card, refund);
            }
            string email = b.Email;
            int points = (int)(tickets / 10);
            LegacyDb.Loyalty[email] = Math.Max(0, LegacyDb.Loyalty.GetValueOrDefault(email, 0) - points);
        }
        _notifications.BookingCancelled(b.Email, bookingId, refund);
        return "REFUND " + Fmt(refund);
    }

    public void ExpireOld()
    {
        DateTime now = Clock();
        foreach (var b in _bookings.All())
        {
            if (b.Status == 0
                && (long)(now - b.CreatedAt).TotalMinutes >= 15)
            {
                b.Status = 3;
                var s = LegacyDb.Screenings[b.ScreeningId];
                var taken = (HashSet<string>)s[6]!;
                foreach (var seat in b.Seats)
                {
                    taken.Remove(seat);
                }
                _notifications.BookingExpired(b.Email, b.Id);
            }
        }
    }

    public string Use(string bookingId)
    {
        var b = _bookings.Find(bookingId);
        if (b == null)
        {
            return "ERROR: no booking";
        }
        if (b.Status != 1)
        {
            return "ERROR: not paid";
        }
        b.Status = 2;
        return "OK";
    }

    public int LoyaltyPoints(string email)
    {
        return LegacyDb.Loyalty.GetValueOrDefault(email, 0);
    }

    public string DailyReport(DateOnly day)
    {
        return _reports.DailyReport(day);
    }

    public string Settlement(string title, int week)
    {
        return _reports.Settlement(title, week);
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
        return Formats.Amount(value);
    }
}
