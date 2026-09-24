namespace Training.Workshop.M7.S13GodClass.Step2;

/// <summary>
/// Krok 2: jedyne miejsce, które zna treść powiadomień i statyczny LegacyMailer.
/// Kolejny krok kampanii mógłby tu wprowadzić seam (interfejs mailera) - bez dotykania CinemaManager.
/// </summary>
internal sealed class NotificationService
{
    internal void BookingCreated(string email, string bookingId, string title, string[] seats, double total)
    {
        LegacyMailer.Send(email, "Rezerwacja " + bookingId,
            "Film: " + title + ", miejsca: " + string.Join(",", seats)
            + ", do zaplaty: " + Formats.Amount(total));
    }

    internal void PaymentDeclined(string email, string bookingId)
    {
        LegacyMailer.Send(email, "Platnosc odrzucona", "Rezerwacja " + bookingId);
    }

    internal void TicketsPaid(string email, string? phone, string bookingId, double paid, int points)
    {
        LegacyMailer.Send(email, "Bilety " + bookingId, "Oplacono " + Formats.Amount(paid)
            + ", punkty: +" + points);
        if (phone != null)
        {
            LegacyMailer.Sms(phone, "CineLegacy: bilety " + bookingId + " oplacone");
        }
    }

    internal void BookingExpired(string email, string bookingId)
    {
        LegacyMailer.Send(email, "Rezerwacja wygasla", bookingId);
    }

    internal void BookingCancelled(string email, string bookingId, double refund)
    {
        LegacyMailer.Send(email, "Anulowano " + bookingId, "Zwrot: " + Formats.Amount(refund));
    }
}
