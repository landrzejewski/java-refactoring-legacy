namespace Training.Workshop.M5.S30Facade.Step1;

/// <summary>Podsystem: zajętość miejsc. Rezerwacja jest atomowa - albo wszystkie miejsca, albo żadne.</summary>
public sealed class SeatRegistry
{
    private readonly SortedDictionary<string, SortedSet<string>> _taken = new(StringComparer.Ordinal);

    public void Reserve(string screening, IReadOnlyList<string> seats)
    {
        if (!_taken.TryGetValue(screening, out var current))
        {
            current = new SortedSet<string>(StringComparer.Ordinal);
            _taken[screening] = current;
        }
        foreach (var seat in seats)
        {
            if (current.Contains(seat))
            {
                throw new InvalidOperationException("seat taken " + seat);
            }
        }
        current.UnionWith(seats);
    }

    public void Release(string screening, IReadOnlyList<string> seats)
    {
        if (_taken.TryGetValue(screening, out var current))
        {
            current.ExceptWith(seats);
        }
    }

    /// <summary>Zapis jak w Javie (TreeMap.toString): <c>{S1=[A1, A2], S2=[]}</c>.</summary>
    public string Describe()
    {
        return "{" + string.Join(", ", _taken.Select(entry => entry.Key + "=[" + string.Join(", ", entry.Value) + "]")) + "}";
    }
}
