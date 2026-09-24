namespace Training.Workshop.M6.S18CollectingParameter.Step2;

/// <summary>
/// Krok 2: Move Accumulation to Collecting Parameter - metody dopisują do przekazanej listy
/// zamiast zwracać fragmenty. Nowa reguła to nowa metoda Check...(draft, warnings).
/// </summary>
public sealed class ReservationValidator
{
    public string Validate(ReservationDraft draft)
    {
        var warnings = new List<string>();
        CheckEmail(draft.Email, warnings);
        CheckSeats(draft.Seats, warnings);
        CheckShowTime(draft, warnings);
        return warnings.Count == 0 ? "OK" : string.Join("; ", warnings);
    }

    private static void CheckEmail(string? email, List<string> warnings)
    {
        if (string.IsNullOrWhiteSpace(email))
        {
            warnings.Add("brak e-maila");
        }
        else if (!email.Contains('@'))
        {
            warnings.Add("niepoprawny e-mail: " + email);
        }
    }

    private static void CheckSeats(IReadOnlyList<string> seats, List<string> warnings)
    {
        if (seats.Count == 0)
        {
            warnings.Add("brak miejsc");
            return;
        }
        var seen = new HashSet<string>();
        var reported = new HashSet<string>();
        foreach (var seat in seats)
        {
            if (!seen.Add(seat) && reported.Add(seat))
            {
                warnings.Add("miejsce " + seat + " zdublowane");
            }
        }
        if (seats.Count >= 10)
        {
            warnings.Add("grupa 10+: zastosuj rabat grupowy");
        }
    }

    private static void CheckShowTime(ReservationDraft draft, List<string> warnings)
    {
        if (draft.Now >= draft.ShowStart)
        {
            warnings.Add("seans juz sie rozpoczal");
        }
    }
}
