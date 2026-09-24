namespace Training.Workshop.M5.S08Composition.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Encapsulate Collection - zamiast delegata klient dostaje niemodyfikowalną kopię
/// w kolejności wyboru. Wąska fasada: tylko operacje, których klienci naprawdę używają.
/// Świadomie tracimy: przypisywalność do ISet, Remove/Clear/IntersectWith, SetEquals zbioru.
/// </summary>
public sealed class SeatSelection
{
    private readonly HashSet<string> _seats = [];
    private readonly List<string> _order = [];
    private int _clicks;

    public bool Add(string seat)
    {
        _clicks++;
        if (!_seats.Add(seat))
        {
            return false;
        }
        _order.Add(seat);
        return true;
    }

    public void UnionWith(IEnumerable<string> seats)
    {
        foreach (var seat in seats)
        {
            Add(seat);
        }
    }

    public bool Contains(string seat)
    {
        return _seats.Contains(seat);
    }

    public int Count => _seats.Count;

    public int Clicks => _clicks;

    public IReadOnlyList<string> Seats => _order.ToList().AsReadOnly();
}
