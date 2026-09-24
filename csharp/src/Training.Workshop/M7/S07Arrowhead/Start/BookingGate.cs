namespace Training.Workshop.M7.S07Arrowhead.Start;

/// <summary>
/// Start: grot strzały - główna ścieżka (BOOKED) schowana na piątym poziomie zagnieżdżenia,
/// wynik zbierany w zmiennej result. Uwaga: wpis do audytu na końcu dotyczy KAŻDEJ ścieżki.
/// </summary>
public sealed class BookingGate
{
    private readonly List<string> _audit = [];

    public string Book(BookingAttempt attempt)
    {
        string result;
        if (attempt.ScreeningFound)
        {
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
        }
        else
        {
            result = "NO_SCREENING";
        }
        _audit.Add(attempt.Email + " -> " + result);
        return result;
    }

    public IReadOnlyList<string> Audit => _audit.ToList();
}
