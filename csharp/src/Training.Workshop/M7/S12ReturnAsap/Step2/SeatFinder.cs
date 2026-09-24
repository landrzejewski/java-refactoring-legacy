namespace Training.Workshop.M7.S12ReturnAsap.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Return ASAP w pętli - zwracamy tam, gdzie wynik jest ostateczny.
/// Flaga found i zmienna result zniknęły. _inspected++ zostaje PRZED return (mutacja zachowana),
/// a Count/seats[index] zostają - foreach (enumerator) zmieniłby sposób dostępu do listy.
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
        for (var index = 0; index < seats.Count; index++)
        {
            var seat = seats[index];
            _inspected++;
            if (seat.Row >= minRow && !seat.Taken)
            {
                return seat.Label;
            }
        }
        return null;
    }

    public int Inspected => _inspected;
}
