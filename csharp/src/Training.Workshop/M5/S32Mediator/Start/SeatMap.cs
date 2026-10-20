using System.Globalization;

namespace Training.Workshop.M5.S32Mediator.Start;

/// <summary>
/// Start: mapa miejsc (sala A-C x 1-4, B2 sprzedane wcześniej). Po kliknięciu sama aktualizuje
/// koszyk, podsumowanie i przycisk - zna trzy inne komponenty i kolejność ich odświeżania.
/// </summary>
public sealed class SeatMap
{
    private readonly SortedSet<string> _hall = Hall();
    private readonly SortedSet<string> _sold = new(["B2"], StringComparer.Ordinal);
    private readonly SortedSet<string> _selected = new(StringComparer.Ordinal);
    private Basket _basket = null!;
    private PriceSummary _summary = null!;
    private PayButton _payButton = null!;

    internal void Connect(Basket basket, PriceSummary summary, PayButton payButton)
    {
        _basket = basket;
        _summary = summary;
        _payButton = payButton;
    }

    /// <summary>Akcja użytkownika: zaznacza albo odznacza miejsce; sprzedane i spoza sali są ignorowane.</summary>
    public void Click(string seat)
    {
        if (!_hall.Contains(seat) || _sold.Contains(seat))
        {
            return;
        }
        if (_selected.Remove(seat))
        {
            _basket.Drop(seat);
        }
        else
        {
            _selected.Add(seat);
            _basket.Add(seat);
        }
        _summary.Recalculate(_basket.Seats);
        _payButton.Refresh(_basket.Seats.Count, _summary.Total);
    }

    internal void Deselect(string seat)
    {
        _selected.Remove(seat);
    }

    internal void DeselectAll()
    {
        _selected.Clear();
    }

    internal void MarkSold(IReadOnlyList<string> seats)
    {
        _sold.UnionWith(seats);
        foreach (var seat in seats)
        {
            _selected.Remove(seat);
        }
    }

    internal string Describe()
    {
        return "wybrane=[" + string.Join(", ", _selected) + "] sprzedane=[" + string.Join(", ", _sold) + "]";
    }

    private static SortedSet<string> Hall()
    {
        var seats = new SortedSet<string>(StringComparer.Ordinal);
        for (var row = 'A'; row <= 'C'; row++)
        {
            for (var number = 1; number <= 4; number++)
            {
                seats.Add(row + number.ToString(CultureInfo.InvariantCulture));
            }
        }
        return seats;
    }
}
