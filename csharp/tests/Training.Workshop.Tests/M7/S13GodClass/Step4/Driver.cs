using Training.Workshop.M7.S13GodClass.Step4;

namespace Training.Workshop.Tests.M7.S13GodClass.Step4;

/// <summary>Adapter wariantu step4 dla S13Script (InternalsVisibleTo - dostęp do haka CinemaManager.Clock).</summary>
public sealed class Driver : ICinemaUnderTest
{
    private CinemaManager _cinema = new();

    public void Reset()
    {
        LegacyDb.Clear();
        _cinema = new CinemaManager();
    }

    public void Clock(Func<DateTime> clock)
    {
        CinemaManager.Clock = clock;
    }

    public void AddScreening(string id, string title, int format, DateTime start, int rows, int seatsPerRow,
        int vipFromRow)
    {
        _cinema.AddScreening(id, title, format, start, rows, seatsPerRow, vipFromRow);
    }

    public string Book(string screeningId, string email, string? phone, string[]? seats, string[]? types, bool web,
        bool ownGlasses)
    {
        return _cinema.Book(screeningId, email, phone, seats, types, web, ownGlasses);
    }

    public string Pay(string bookingId, string? card)
    {
        return _cinema.Pay(bookingId, card);
    }

    public string Cancel(string bookingId)
    {
        return _cinema.Cancel(bookingId);
    }

    public void ExpireOld()
    {
        _cinema.ExpireOld();
    }

    public string Use(string bookingId)
    {
        return _cinema.Use(bookingId);
    }

    public int LoyaltyPoints(string email)
    {
        return _cinema.LoyaltyPoints(email);
    }

    public IReadOnlyList<string> FreeSeats(string screeningId)
    {
        return _cinema.FreeSeats(screeningId);
    }

    public string DailyReport(DateOnly day)
    {
        return _cinema.DailyReport(day);
    }

    public string Settlement(string title, int week)
    {
        return _cinema.Settlement(title, week);
    }

    public IReadOnlyList<string> SentMessages()
    {
        return LegacyMailer.Sent.ToList();
    }

    public IReadOnlyList<string> GatewayOperations()
    {
        return LegacyPaymentGateway.Charges.ToList();
    }
}
