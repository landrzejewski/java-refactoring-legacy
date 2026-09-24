using Training.Workshop.M3.S12CleanArchitecture.Step4.Adapter;
using Training.Workshop.M3.S12CleanArchitecture.Step4.App;

namespace Training.Workshop.M3.S12CleanArchitecture.Step4;

/// <summary>
/// Krok 4 (rozwiązanie): composition root - jedyne miejsce, które zna wszystkie konkrety
/// i składa graf ręcznymi konstruktorami. Nie zawiera reguł biznesowych.
/// Podmiana adaptera (np. inna baza) = zmiana tylko tutaj.
/// </summary>
public static class CinemaApplication
{
    public static ReservationController ReservationController(RowStore db, Outbox outbox)
    {
        var bookSeats = new BookSeats(
            new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
        return new ReservationController(bookSeats);
    }
}
