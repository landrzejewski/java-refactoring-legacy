namespace Training.Workshop.M6.S18CollectingParameter.Step3;

/// <summary>
/// Krok 3: parametr zbierający ma własny, wąski typ Warnings (tylko Add). Właścicielem
/// kolekcji jest Validate - tworzy ją i decyduje o formacie wyniku.
/// </summary>
public sealed class ReservationValidator
{
    public string Validate(ReservationDraft draft)
    {
        var warnings = new Warnings();
        CheckEmail(draft.Email, warnings);
        CheckSeats(draft.Seats, warnings);
        CheckShowTime(draft, warnings);
        return warnings.Summary();
    }

    private static void CheckEmail(string? email, Warnings warnings)
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

    private static void CheckSeats(IReadOnlyList<string> seats, Warnings warnings)
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

    private static void CheckShowTime(ReservationDraft draft, Warnings warnings)
    {
        if (draft.Now >= draft.ShowStart)
        {
            warnings.Add("seans juz sie rozpoczal");
        }
    }
}
