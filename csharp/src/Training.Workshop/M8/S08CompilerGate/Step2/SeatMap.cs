using System.Globalization;

namespace Training.Workshop.M8.S08CompilerGate.Step2;

/// <summary>Krok 2 (bez zmian): mapa miejsc na typach generycznych.</summary>
public sealed class SeatMap
{
    private readonly SortedDictionary<int, List<string>> _seatsByRow = new();

    public void Take(string seat)
    {
        int row = int.Parse(seat.Substring(1), CultureInfo.InvariantCulture);
        if (!_seatsByRow.TryGetValue(row, out List<string>? seats))
        {
            seats = [];
            _seatsByRow[row] = seats;
        }
        seats.Add(seat);
    }

    public IReadOnlyDictionary<int, int> TakenPerRow()
    {
        var result = new SortedDictionary<int, int>();
        foreach (var (row, seats) in _seatsByRow)
        {
            result[row] = seats.Count;
        }
        return result;
    }
}
