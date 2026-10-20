namespace Training.Workshop.M5.S32Mediator.Start;

/// <summary>
/// Start: koszyk (miejsca w kolejności wyboru). Usunięcie i czyszczenie same
/// odświeżają mapę, sumę i przycisk.
/// </summary>
public sealed class Basket
{
    private readonly List<string> _seats = [];
    private SeatMap _seatMap = null!;
    private PriceSummary _summary = null!;
    private PayButton _payButton = null!;

    internal void Connect(SeatMap seatMap, PriceSummary summary, PayButton payButton)
    {
        _seatMap = seatMap;
        _summary = summary;
        _payButton = payButton;
    }

    /// <summary>Akcja użytkownika: usuwa miejsce z koszyka (i odznacza je na mapie).</summary>
    public void Remove(string seat)
    {
        if (!_seats.Remove(seat))
        {
            return;
        }
        _seatMap.Deselect(seat);
        _summary.Recalculate(_seats);
        _payButton.Refresh(_seats.Count, _summary.Total);
    }

    /// <summary>Akcja użytkownika: czyści koszyk. Woła ją też przycisk po płatności.</summary>
    public void Clear()
    {
        _seats.Clear();
        _seatMap.DeselectAll();
        _summary.Recalculate(_seats);
        _payButton.Refresh(_seats.Count, _summary.Total);
    }

    internal void Add(string seat)
    {
        _seats.Add(seat);
    }

    internal void Drop(string seat)
    {
        _seats.Remove(seat);
    }

    internal IReadOnlyList<string> Seats => _seats.ToList();

    internal string Describe()
    {
        return "koszyk=[" + string.Join(", ", _seats) + "]";
    }
}
