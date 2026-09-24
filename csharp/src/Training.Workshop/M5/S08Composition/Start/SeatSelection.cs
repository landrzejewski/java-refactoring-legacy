namespace Training.Workshop.M5.S08Composition.Start;

/// <summary>
/// Start: wybór miejsc dziedziczy po HashSet tylko po to, by mieć Add/Contains za darmo,
/// i liczy kliknięcia do analityki. Metody HashSet nie są wirtualne, więc licznik da się dołożyć
/// tylko przez ukrycie (<c>new</c>), a nie nadpisanie: wywołanie przez typ bazowy albo interfejs
/// (HashSet&lt;string&gt;, ISet&lt;string&gt;, ICollection&lt;string&gt;) trafia prosto do HashSet i omija licznik.
/// Do tego klient dostaje całe API zbioru (Remove, Clear, IntersectWith...), które też omija licznik.
/// </summary>
public class SeatSelection : HashSet<string>
{
    private int _clicks;

    public new bool Add(string seat)
    {
        _clicks++;
        return base.Add(seat);
    }

    public new void UnionWith(IEnumerable<string> seats)
    {
        var more = seats.ToList();
        _clicks += more.Count;
        base.UnionWith(more);
    }

    public int Clicks => _clicks;
}
