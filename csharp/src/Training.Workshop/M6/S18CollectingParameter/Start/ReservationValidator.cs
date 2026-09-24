namespace Training.Workshop.M6.S18CollectingParameter.Start;

/// <summary>
/// Start: ostrzeżenia sklejane w string - każda metoda pomocnicza zwraca fragment z "; ",
/// a na końcu obcinamy dwa ostatnie znaki. Łatwo o zgubiony separator.
/// </summary>
public sealed class ReservationValidator
{
    public string Validate(ReservationDraft draft)
    {
        var warnings = "";
        warnings += CheckEmail(draft.Email);
        warnings += CheckSeats(draft.Seats);
        if (draft.Now >= draft.ShowStart)
        {
            warnings += "seans juz sie rozpoczal; ";
        }
        return warnings.Length == 0 ? "OK" : warnings[..^2];
    }

    private static string CheckEmail(string? email)
    {
        if (string.IsNullOrWhiteSpace(email))
        {
            return "brak e-maila; ";
        }
        if (!email.Contains('@'))
        {
            return "niepoprawny e-mail: " + email + "; ";
        }
        return "";
    }

    private static string CheckSeats(IReadOnlyList<string> seats)
    {
        if (seats.Count == 0)
        {
            return "brak miejsc; ";
        }
        var result = "";
        var seen = new HashSet<string>();
        var reported = new HashSet<string>();
        foreach (var seat in seats)
        {
            if (!seen.Add(seat) && reported.Add(seat))
            {
                result += "miejsce " + seat + " zdublowane; ";
            }
        }
        if (seats.Count >= 10)
        {
            result += "grupa 10+: zastosuj rabat grupowy; ";
        }
        return result;
    }
}
