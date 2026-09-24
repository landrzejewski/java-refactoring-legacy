namespace Training.Workshop.M6.S05ExtractFactory.Step2;

/// <summary>
/// Krok 2: serwis deleguje tworzenie do fabryki; konstruktor bez zmian, więc klienci
/// serwisu niczego nie zauważają.
/// </summary>
public sealed class ReservationService
{
    private readonly ReservationFactory _factory;
    private readonly HashSet<string> _takenSeats = [];

    public ReservationService(TimeProvider clock)
    {
        _factory = new ReservationFactory(clock);
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
