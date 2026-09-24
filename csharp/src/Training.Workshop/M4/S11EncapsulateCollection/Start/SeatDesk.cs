namespace Training.Workshop.M4.S11EncapsulateCollection.Start;

/// <summary>Klient kolekcji: wybór i zwolnienie miejsca - bezpośrednio na liście właściciela.</summary>
public sealed class SeatDesk
{
    public void Select(Booking booking, Seat seat)
    {
        booking.Seats.Add(seat);
    }

    public void Release(Booking booking, Seat seat)
    {
        booking.Seats.Remove(seat);
    }
}
