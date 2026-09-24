namespace Training.Workshop.M5.S08Composition.Step1;

/// <summary>
/// Krok 1: Replace Inheritance with Delegation (wygenerowane przez IDE). Zbiór jest prywatnym delegatem,
/// nie ma już typu bazowego ani interfejsu, przez który dałoby się ominąć Add()/UnionWith() z licznikiem.
/// Pozostała pułapka delegowania: wygenerowana właściwość wydaje delegata, więc da się go zmienić z zewnątrz.
/// </summary>
public class SeatSelection
{
    private readonly HashSet<string> _seats = [];
    private int _clicks;

    public bool Add(string seat)
    {
        _clicks++;
        return _seats.Add(seat);
    }

    public void UnionWith(IEnumerable<string> seats)
    {
        var more = seats.ToList();
        _clicks += more.Count;
        _seats.UnionWith(more);
    }

    public bool Contains(string seat)
    {
        return _seats.Contains(seat);
    }

    public int Count => _seats.Count;

    public int Clicks => _clicks;

    public HashSet<string> SeatSet => _seats;
}
