namespace Training.Workshop.M6.S18CollectingParameter.Step1;

/// <summary>
/// Krok 1: string zastąpiony listą - separator dokłada tylko string.Join. Metody wciąż
/// tworzą własne listy, które Validate skleja przez AddRange.
/// </summary>
public sealed class ReservationValidator
{
    public string Validate(ReservationDraft draft)
    {
        var warnings = new List<string>();
        warnings.AddRange(CheckEmail(draft.Email));
        warnings.AddRange(CheckSeats(draft.Seats));
        if (draft.Now >= draft.ShowStart)
        {
            warnings.Add("seans juz sie rozpoczal");
        }
        return warnings.Count == 0 ? "OK" : string.Join("; ", warnings);
    }

    private static IReadOnlyList<string> CheckEmail(string? email)
    {
        if (string.IsNullOrWhiteSpace(email))
        {
            return ["brak e-maila"];
        }
        if (!email.Contains('@'))
        {
            return ["niepoprawny e-mail: " + email];
        }
        return [];
    }

    private static IReadOnlyList<string> CheckSeats(IReadOnlyList<string> seats)
    {
        if (seats.Count == 0)
        {
            return ["brak miejsc"];
        }
        var result = new List<string>();
        var seen = new HashSet<string>();
        var reported = new HashSet<string>();
        foreach (var seat in seats)
        {
            if (!seen.Add(seat) && reported.Add(seat))
            {
                result.Add("miejsce " + seat + " zdublowane");
            }
        }
        if (seats.Count >= 10)
        {
            result.Add("grupa 10+: zastosuj rabat grupowy");
        }
        return result;
    }
}
