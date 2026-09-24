using Training.Workshop.Shared;

namespace Training.Workshop.M6.S05ExtractFactory.Step1;

/// <summary>
/// Krok 1: Extract Method - tworzenie rezerwacji w jednej metodzie NewReservation.
/// Kolejność zachowana: numer jest pobierany PRZED walidacją kanału (błąd "spala" numer).
/// </summary>
public sealed class ReservationService
{
    private readonly TimeProvider _clock;
    private readonly HashSet<string> _takenSeats = [];
    private int _sequence;

    public ReservationService(TimeProvider clock)
    {
        _clock = clock;
    }

    public Reservation Reserve(string channel, string email, IReadOnlyList<string> seats)
    {
        RequireFree(seats);
        var reservation = NewReservation(channel, email, seats);
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
        var reservation = NewReservation("ONLINE", email, seats);
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

    private Reservation NewReservation(string channel, string email, IReadOnlyList<string> seats)
    {
        _sequence++;
        var id = "R" + _sequence;
        if (channel != "ONLINE" && channel != "BOX_OFFICE")
        {
            throw new ArgumentException("unknown channel: " + channel);
        }
        var fee = channel == "ONLINE" ? Money.Of("2.00").Times(seats.Count) : Money.Zero;
        DateTime? expiresAt = channel == "ONLINE"
            ? _clock.GetLocalNow().DateTime.AddMinutes(15)
            : null;
        return new Reservation(id, channel, email, seats.ToList(), fee, expiresAt);
    }
}
