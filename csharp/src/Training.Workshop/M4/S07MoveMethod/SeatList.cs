namespace Training.Workshop.M4.S07MoveMethod;

/// <summary>
/// Lista miejsc ze starego API (port biblioteki z Javy) - stabilny typ sceny.
/// Ma dwa przeciążenia <c>Remove</c> jak <c>java.util.List</c>: po indeksie (<c>int</c>)
/// i po numerze miejsca (<c>int?</c>). Które się wywoła, decyduje typ argumentu.
/// </summary>
public sealed class SeatList : IEnumerable<int>
{
    private readonly List<int> _seats;

    public SeatList(IEnumerable<int> seats)
    {
        _seats = [.. seats];
    }

    /// <summary>Usuwa element na pozycji <paramref name="index"/>.</summary>
    public void Remove(int index)
    {
        _seats.RemoveAt(index);
    }

    /// <summary>Usuwa miejsce o numerze <paramref name="seat"/> (jeśli jest na liście).</summary>
    public void Remove(int? seat)
    {
        if (seat is int number)
        {
            _seats.Remove(number);
        }
    }

    public IEnumerator<int> GetEnumerator() => _seats.GetEnumerator();

    System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator() => GetEnumerator();

    public override string ToString() => "[" + string.Join(", ", _seats) + "]";
}
