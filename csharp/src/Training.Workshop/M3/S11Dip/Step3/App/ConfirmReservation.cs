using System.Globalization;

namespace Training.Workshop.M3.S11Dip.Step3.App;

/// <summary>
/// Krok 3 (rozwiązanie): Extract Interface z potrzeby + Move Method do adaptera.
/// Polityka nie używa niczego z Infra. Sterowanie nadal płynie App -&gt; Infra
/// (Confirm woła notifier), ale zależność źródłowa odwróciła się: Infra -&gt; App.
/// </summary>
public sealed class ConfirmReservation
{
    private readonly ICustomerNotifier _notifier;

    public ConfirmReservation(ICustomerNotifier notifier)
    {
        _notifier = notifier;
    }

    public string Confirm(Reservation reservation)
    {
        if (reservation.Seats < 1)
        {
            throw new ArgumentException("rezerwacja bez miejsc");
        }
        var message = "Rezerwacja: " + reservation.Title + ", "
            + reservation.Start.ToString("yyyy-MM-dd'T'HH:mm", CultureInfo.InvariantCulture)
            + ", miejsc: " + reservation.Seats + ". Zaplac w ciagu 15 minut.";
        _notifier.NotifyCustomer(reservation.Email, message);
        return "potwierdzono: " + reservation.Email;
    }
}
