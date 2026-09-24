using System.Collections;
using System.Globalization;

namespace Training.Workshop.M8.S08CompilerGate.Start;

/// <summary>
/// Start: mapa zajętych miejsc na kolekcjach niegenerycznych (Hashtable, ArrayList) z rzutowaniami -
/// kompilator nie zna typów elementów, więc analiza nullable zgłasza ostrzeżenia CS8600 i CS8602.
/// </summary>
public sealed class SeatMap
{
    private readonly Hashtable _seatsByRow = new();

    public void Take(string seat)
    {
        object row = int.Parse(seat.Substring(1), CultureInfo.InvariantCulture);
        ArrayList seats = (ArrayList)_seatsByRow[row];
        if (seats == null)
        {
            seats = new ArrayList();
            _seatsByRow[row] = seats;
        }
        seats.Add(seat);
    }

    public IReadOnlyDictionary<int, int> TakenPerRow()
    {
        var result = new SortedDictionary<int, int>();
        foreach (object row in _seatsByRow.Keys)
        {
            result[(int)row] = ((ArrayList)_seatsByRow[row]).Count;
        }
        return result;
    }
}
