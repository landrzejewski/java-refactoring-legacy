using System.Globalization;
using Training.Workshop.M3.S12CleanArchitecture.Step3.App;

namespace Training.Workshop.M3.S12CleanArchitecture.Step3.Adapter;

/// <summary>Krok 2: adapter wyjściowy - zna temat i format komunikatu.</summary>
public sealed class OutboxBookingNotifier : IBookingNotifier
{
    private readonly Outbox _outbox;

    public OutboxBookingNotifier(Outbox outbox)
    {
        _outbox = outbox;
    }

    public void ReservationCreated(string id, NewReservation reservation)
    {
        _outbox.Publish("reservation-created",
            id + ";" + reservation.Email + ";" + reservation.Total.ToString(CultureInfo.InvariantCulture));
    }
}
