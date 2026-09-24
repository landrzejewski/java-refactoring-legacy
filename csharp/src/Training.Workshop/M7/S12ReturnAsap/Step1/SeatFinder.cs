namespace Training.Workshop.M7.S12ReturnAsap.Step1;

/// <summary>
/// Krok 1: guard clauses - w SeatClass() przypadki kończące (brak, zajęte) zwracają od razu,
/// zmienna result zniknęła. W FirstFree() guard dla null zamiast otaczającego if.
/// </summary>
public sealed class SeatFinder
{
    private int _inspected;

    public string SeatClass(Seat? seat, int vipFromRow)
    {
        if (seat == null)
        {
            return "BRAK";
        }
        if (seat.Taken)
        {
            return "ZAJETE";
        }
        if (seat.Row >= vipFromRow)
        {
            return "VIP";
        }
        return "STANDARD";
    }

    public string? FirstFree(IReadOnlyList<Seat>? seats, int minRow)
    {
        if (seats == null)
        {
            return null;
        }
        string? result = null;
        var found = false;
        var index = 0;
        while (!found && index < seats.Count)
        {
            var seat = seats[index];
            _inspected++;
            if (seat.Row >= minRow && !seat.Taken)
            {
                result = seat.Label;
                found = true;
            }
            index++;
        }
        return result;
    }

    public int Inspected => _inspected;
}
