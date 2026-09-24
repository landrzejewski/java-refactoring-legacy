using System.Globalization;
using Training.Workshop.M3.S11Dip.Step2.Infra;

namespace Training.Workshop.M3.S11Dip.Step2.App;

/// <summary>
/// Krok 2: Extract Method - potrzeba polityki dostaje nazwę w języku problemu:
/// <c>NotifyCustomer(email, message)</c>. Cały "technologiczny" kod (MIME, kody SMTP)
/// trafił do tej jednej metody. Tak wygląda port, zanim stanie się interfejsem.
/// </summary>
public sealed class ConfirmReservation
{
    private readonly SmtpMailSender _mail;

    public ConfirmReservation(SmtpMailSender mail)
    {
        _mail = mail;
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
        NotifyCustomer(reservation.Email, message);
        return "potwierdzono: " + reservation.Email;
    }

    private void NotifyCustomer(string email, string message)
    {
        var mime = "To: " + email + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
        var reply = _mail.Send(email, mime);
        if (!reply.StartsWith("250", StringComparison.Ordinal))
        {
            throw new InvalidOperationException("SMTP odrzucil: " + reply);
        }
    }
}
