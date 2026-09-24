namespace Training.Workshop.M7.S07Arrowhead.Step2;

/// <summary>
/// Krok 2: odwrócenie najbardziej zewnętrznego warunku - pierwsza guard clause
/// (brak seansu) i usunięcie jednego poziomu else. Test po każdym poziomie.
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
        string result;
        if (attempt.SalesOpen)
        {
            if (!attempt.CustomerBlocked)
            {
                if (attempt.RequestedSeats > 0)
                {
                    if (attempt.RequestedSeats <= attempt.FreeSeats)
                    {
                        result = "BOOKED";
                    }
                    else
                    {
                        result = "SOLD_OUT";
                    }
                }
                else
                {
                    result = "NO_SEATS_REQUESTED";
                }
            }
            else
            {
                result = "CUSTOMER_BLOCKED";
            }
        }
        else
        {
            result = "SALES_CLOSED";
        }
        return result;
    }

    public IReadOnlyList<string> Audit => _audit.ToList();
}
