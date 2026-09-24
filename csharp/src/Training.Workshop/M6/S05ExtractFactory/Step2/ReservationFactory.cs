using Training.Workshop.Shared;

namespace Training.Workshop.M6.S05ExtractFactory.Step2;

/// <summary>
/// Krok 2: Extract Factory Class - cała wiedza o budowie rezerwacji (numeracja, opłata,
/// termin ważności) w jednej klasie. Pole _sequence i zegar przeniesione razem z metodą.
/// </summary>
public sealed class ReservationFactory
{
    private readonly TimeProvider _clock;
    private int _sequence;

    public ReservationFactory(TimeProvider clock)
    {
        _clock = clock;
    }

    public Reservation Create(string channel, string email, IReadOnlyList<string> seats)
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
