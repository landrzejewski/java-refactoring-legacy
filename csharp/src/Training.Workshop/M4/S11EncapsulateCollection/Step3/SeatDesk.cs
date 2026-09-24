namespace Training.Workshop.M4.S11EncapsulateCollection.Step3;

/// <summary>Od kroku 1: klient przepisany na operacje właściciela - nie dotyka już listy.</summary>
public sealed class SeatDesk
{
    public void Select(Booking booking, Seat seat)
    {
        booking.AddSeat(seat);
    }

    public void Release(Booking booking, Seat seat)
    {
        booking.RemoveSeat(seat);
    }
}
