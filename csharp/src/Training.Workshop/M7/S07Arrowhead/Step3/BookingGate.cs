namespace Training.Workshop.M7.S07Arrowhead.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): pozostałe poziomy spłaszczone do guard clauses, zmienna result
/// zniknęła. Kolejność warunków identyczna jak w start - priorytet błędów zachowany.
/// </summary>
public sealed class BookingGate
{
    private readonly List<string> _audit = [];

    public string Book(BookingAttempt attempt)
    {
        var result = Decide(attempt);
        _audit.Add(attempt.Email + " -> " + result);
        return result;
    }

    private static string Decide(BookingAttempt attempt)
    {
        if (!attempt.ScreeningFound)
        {
            return "NO_SCREENING";
        }
        if (!attempt.SalesOpen)
        {
            return "SALES_CLOSED";
        }
        if (attempt.CustomerBlocked)
        {
            return "CUSTOMER_BLOCKED";
        }
        if (attempt.RequestedSeats <= 0)
        {
            return "NO_SEATS_REQUESTED";
        }
        if (attempt.RequestedSeats > attempt.FreeSeats)
        {
            return "SOLD_OUT";
        }
        return "BOOKED";
    }

    public IReadOnlyList<string> Audit => _audit.ToList();
}
