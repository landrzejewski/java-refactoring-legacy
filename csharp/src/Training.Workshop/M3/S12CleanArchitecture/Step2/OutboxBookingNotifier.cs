using System.Globalization;

namespace Training.Workshop.M3.S12CleanArchitecture.Step2;

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
