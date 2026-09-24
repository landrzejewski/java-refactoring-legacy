namespace Training.Workshop.M7.S12ReturnAsap.Start;

/// <summary>
/// Start: "jeden punkt wyjścia" - wynik niesiony w zmiennej result i fladze found,
/// zagnieżdżone if-y. Licznik Inspected (metryka dla działu IT) jest efektem ubocznym,
/// który musi przetrwać każdą zmianę.
/// </summary>
public sealed class SeatFinder
{
    private int _inspected;

    public string SeatClass(Seat? seat, int vipFromRow)
    {
        var result = "STANDARD";
        if (seat != null)
        {
            if (!seat.Taken)
            {
                if (seat.Row >= vipFromRow)
                {
                    result = "VIP";
                }
            }
            else
            {
                result = "ZAJETE";
            }
        }
        else
        {
            result = "BRAK";
        }
        return result;
    }

    public string? FirstFree(IReadOnlyList<Seat>? seats, int minRow)
    {
        string? result = null;
        if (seats != null)
        {
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
        }
        return result;
    }

    public int Inspected => _inspected;
}
