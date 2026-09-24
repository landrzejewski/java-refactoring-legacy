namespace Training.Workshop.M6.S05ExtractFactory.Step3;

/// <summary>
/// Krok 3: fabryka wstrzyknięta przez konstruktor. Test może podać fabrykę z innym zegarem,
/// a numeracja może być współdzielona przez wiele serwisów.
/// </summary>
public sealed class ReservationService
{
    private readonly ReservationFactory _factory;
    private readonly HashSet<string> _takenSeats = [];

    /// <summary>Dotychczasowy konstruktor zostaje jako wygodny skrót - klienci nie muszą się zmieniać.</summary>
    public ReservationService(TimeProvider clock)
        : this(new ReservationFactory(clock))
    {
    }

    public ReservationService(ReservationFactory factory)
    {
        ArgumentNullException.ThrowIfNull(factory);
        _factory = factory;
    }

    public Reservation Reserve(string channel, string email, IReadOnlyList<string> seats)
    {
        RequireFree(seats);
        var reservation = _factory.Create(channel, email, seats);
        _takenSeats.UnionWith(seats);
        return reservation;
    }

    public Reservation ReserveGroup(string email, IReadOnlyList<string> seats)
    {
        if (seats.Count < 10)
        {
            throw new ArgumentException("group needs 10+ seats");
        }
        RequireFree(seats);
        var reservation = _factory.Create("ONLINE", email, seats);
        _takenSeats.UnionWith(seats);
        return reservation;
    }

    private void RequireFree(IReadOnlyList<string> seats)
    {
        foreach (var seat in seats)
        {
            if (_takenSeats.Contains(seat))
            {
                throw new InvalidOperationException("seat taken: " + seat);
            }
        }
    }
}
