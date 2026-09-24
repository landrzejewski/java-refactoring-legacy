namespace Training.Workshop.M7.S07Arrowhead.Step1;

/// <summary>
/// Krok 1: Extract Method - decyzja (cały grot) wydzielona do Decide(), a efekt uboczny
/// (audyt) zostaje w Book(). Teraz wczesne return w Decide() nie ominą audytu.
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
        return result;
    }

    public IReadOnlyList<string> Audit => _audit.ToList();
}
