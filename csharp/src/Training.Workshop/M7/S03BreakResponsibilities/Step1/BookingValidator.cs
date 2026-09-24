using System.Text.RegularExpressions;

namespace Training.Workshop.M7.S03BreakResponsibilities.Step1;

/// <summary>
/// Krok 1: Extract Class - reguły walidacji mają własnego właściciela.
/// Kolejność kontroli bez zmian.
/// </summary>
internal sealed class BookingValidator
{
    internal string? FirstError(BookingRequest request)
    {
        if (request.Email == null || !request.Email.Contains('@'))
        {
            return "ERROR: niepoprawny e-mail";
        }
        if (request.Seats.Count == 0)
        {
            return "ERROR: brak miejsc";
        }
        foreach (var seat in request.Seats)
        {
            if (!Regex.IsMatch(seat, "^[A-L][0-9]{1,2}$"))
            {
                return "ERROR: niepoprawne miejsce " + seat;
            }
        }
        return null;
    }
}
