namespace Training.Workshop.M3.S12CleanArchitecture.Step3.App;

/// <summary>
/// Krok 3: Move Class - przypadek użycia i jego porty w przestrzeni nazw App, adaptery w Adapter.
/// Zależy tylko od własnych portów. Protokół efektów jest
/// jawny: najpierw zapis, potem powiadomienie - błąd zapisu oznacza brak powiadomienia.
/// </summary>
public sealed class BookSeats
{
    private readonly IReservationStore _store;
    private readonly IBookingNotifier _notifier;

    public BookSeats(IReservationStore store, IBookingNotifier notifier)
    {
        _store = store;
        _notifier = notifier;
    }

    public Booking Execute(BookSeatsCommand command)
    {
        if (command.Rows.Count == 0)
        {
            throw new ArgumentException("brak miejsc");
        }
        var reservation = new NewReservation(
            command.Email, command.Format, command.Rows.Count, Price(command));
        var id = _store.Save(reservation);
        _notifier.ReservationCreated(id, reservation);
        return new Booking(id, reservation.Total);
    }

    private static decimal Price(BookSeatsCommand command)
    {
        var @base = command.Format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        var total = 0.00m;
        foreach (var row in command.Rows)
        {
            total += @base;
            if (row >= 10)
            {
                total += 10.00m;
            }
        }
        return total;
    }
}
