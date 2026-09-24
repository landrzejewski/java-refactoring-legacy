using System.Globalization;
using Training.Workshop.M3.S11Dip.Step1.Infra;

namespace Training.Workshop.M3.S11Dip.Step1.App;

/// <summary>
/// Krok 1: Introduce Parameter - klient SMTP wstrzyknięty przez konstruktor.
/// To jest dependency injection, ale jeszcze NIE DIP: using wciąż prowadzi
/// z polityki do szczegółu (App -&gt; Infra), a polityka zna MIME i kody SMTP.
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
        var mime = "To: " + reservation.Email + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
        var reply = _mail.Send(reservation.Email, mime);
        if (!reply.StartsWith("250", StringComparison.Ordinal))
        {
            throw new InvalidOperationException("SMTP odrzucil: " + reply);
        }
        return "potwierdzono: " + reservation.Email;
    }
}
