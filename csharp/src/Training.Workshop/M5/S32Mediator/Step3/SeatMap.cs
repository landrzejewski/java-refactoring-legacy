using System.Globalization;

namespace Training.Workshop.M5.S32Mediator.Step3;

/// <summary>Krok 3: bez zmian od kroku 1 - mapa zna tylko mediatora.</summary>
public sealed class SeatMap
{
    private readonly SortedSet<string> _hall = Hall();
    private readonly SortedSet<string> _sold = new(["B2"], StringComparer.Ordinal);
    private readonly SortedSet<string> _selected = new(StringComparer.Ordinal);
    private readonly ISelectionMediator _mediator;

    public SeatMap(ISelectionMediator mediator)
    {
        ArgumentNullException.ThrowIfNull(mediator);
        _mediator = mediator;
    }

    /// <summary>Akcja użytkownika: zaznacza albo odznacza miejsce; sprzedane i spoza sali są ignorowane.</summary>
    public void Click(string seat)
    {
        if (!_hall.Contains(seat) || _sold.Contains(seat))
        {
            return;
        }
        var nowSelected = !_selected.Remove(seat);
        if (nowSelected)
        {
            _selected.Add(seat);
        }
        _mediator.SeatToggled(seat, nowSelected);
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
