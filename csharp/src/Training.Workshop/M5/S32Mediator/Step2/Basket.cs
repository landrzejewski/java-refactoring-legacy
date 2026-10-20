namespace Training.Workshop.M5.S32Mediator.Step2;

/// <summary>Krok 2: koszyk zna tylko mediatora - zgłasza usunięcie i wyczyszczenie, nie odświeża innych.</summary>
public sealed class Basket
{
    private readonly List<string> _seats = [];
    private readonly ISelectionMediator _mediator;

    public Basket(ISelectionMediator mediator)
    {
        ArgumentNullException.ThrowIfNull(mediator);
        _mediator = mediator;
    }

    /// <summary>Akcja użytkownika: usuwa miejsce z koszyka (i odznacza je na mapie).</summary>
    public void Remove(string seat)
    {
        if (!_seats.Remove(seat))
        {
            return;
        }
        _mediator.SeatRemoved(seat);
    }

    /// <summary>Akcja użytkownika: czyści koszyk. Woła ją też mediator po płatności.</summary>
    public void Clear()
    {
        _seats.Clear();
        _mediator.BasketCleared();
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
