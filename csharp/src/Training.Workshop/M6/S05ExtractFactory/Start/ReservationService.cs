using Training.Workshop.Shared;

namespace Training.Workshop.M6.S05ExtractFactory.Start;

/// <summary>
/// Start: serwis pilnuje zajętości miejsc, ale też wie, jak zbudować rezerwację (numer, opłata,
/// termin ważności). Ta wiedza jest skopiowana w Reserve i ReserveGroup.
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
        foreach (var seat in seats)
        {
            if (_takenSeats.Contains(seat))
            {
                throw new InvalidOperationException("seat taken: " + seat);
            }
        }
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
        _takenSeats.UnionWith(seats);
        return new Reservation(id, channel, email, seats.ToList(), fee, expiresAt);
    }

    public Reservation ReserveGroup(string email, IReadOnlyList<string> seats)
    {
        if (seats.Count < 10)
        {
            throw new ArgumentException("group needs 10+ seats");
        }
        foreach (var seat in seats)
        {
            if (_takenSeats.Contains(seat))
            {
                throw new InvalidOperationException("seat taken: " + seat);
            }
        }
        _sequence++;
        var id = "R" + _sequence;
        var fee = Money.Of("2.00").Times(seats.Count);
        var expiresAt = _clock.GetLocalNow().DateTime.AddMinutes(15);
        _takenSeats.UnionWith(seats);
        return new Reservation(id, "ONLINE", email, seats.ToList(), fee, expiresAt);
    }
}
