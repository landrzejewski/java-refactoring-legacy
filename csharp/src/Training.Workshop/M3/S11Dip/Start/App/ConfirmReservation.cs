using System.Globalization;
using Training.Workshop.M3.S11Dip.Start.Infra;

namespace Training.Workshop.M3.S11Dip.Start.App;

/// <summary>
/// Start: polityka (potwierdzenie rezerwacji) sama tworzy szczegół techniczny,
/// składa nagłówki MIME i interpretuje kody SMTP. Zależność źródłowa i przepływ
/// sterowania biegną w tę samą stronę: App -&gt; Infra. Nie da się jej przetestować
/// bez "wysłania maila" i nie da się zmienić kanału (SMS) bez edycji polityki.
/// </summary>
public sealed class ConfirmReservation
{
    private readonly SmtpMailSender _mail = new("smtp.kino.pl", 25);

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
