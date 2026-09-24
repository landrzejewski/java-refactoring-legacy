namespace Training.Workshop.Tests.M7.S13GodClass;

/// <summary>
/// Publiczne API CinemaManager plus dostęp do globalnego stanu i efektów ubocznych.
/// Każdy wariant sceny ma adapter Driver we własnej przestrzeni nazw testów (dostęp do haka Clock).
/// Wszystkie warianty zachowują to samo API - to warunek kampanii Remove God Class.
/// </summary>
public interface ICinemaUnderTest
{
    void Reset();

    void Clock(Func<DateTime> clock);

    void AddScreening(string id, string title, int format, DateTime start, int rows, int seatsPerRow,
        int vipFromRow);

    string Book(string screeningId, string email, string? phone, string[]? seats, string[]? types, bool web,
        bool ownGlasses);

    string Pay(string bookingId, string? card);

    string Cancel(string bookingId);

    void ExpireOld();

    string Use(string bookingId);

    int LoyaltyPoints(string email);

    IReadOnlyList<string> FreeSeats(string screeningId);

    string DailyReport(DateOnly day);

    string Settlement(string title, int week);

    IReadOnlyList<string> SentMessages();

    IReadOnlyList<string> GatewayOperations();
}
