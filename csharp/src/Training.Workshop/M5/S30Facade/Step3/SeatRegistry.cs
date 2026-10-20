namespace Training.Workshop.M5.S30Facade.Step3;

/// <summary>
/// Krok 3: podsystem ukryty (internal) - zajętość miejsc, rezerwacja atomowa.
/// Dostęp tylko przez BookingFacade; w prawdziwym kodzie kanały sprzedaży leżałyby w innym projekcie (assembly).
/// </summary>
internal sealed class SeatRegistry
{
    private readonly SortedDictionary<string, SortedSet<string>> _taken = new(StringComparer.Ordinal);

    internal void Reserve(string screening, IReadOnlyList<string> seats)
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

    internal void Release(string screening, IReadOnlyList<string> seats)
    {
        if (_taken.TryGetValue(screening, out var current))
        {
            current.ExceptWith(seats);
        }
    }

    /// <summary>Zapis jak w Javie (TreeMap.toString): <c>{S1=[A1, A2], S2=[]}</c>.</summary>
    internal string Describe()
    {
        return "{" + string.Join(", ", _taken.Select(entry => entry.Key + "=[" + string.Join(", ", entry.Value) + "]")) + "}";
    }
}
