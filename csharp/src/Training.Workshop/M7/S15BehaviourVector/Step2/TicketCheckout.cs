namespace Training.Workshop.M7.S15BehaviourVector.Step2;

/// <summary>
/// Krok 2: naprawa regresji pod ochroną testu z kroku 1 - mail potwierdzający wraca
/// do gałęzi sukcesu. Wynik metody się nie zmienia, zmienia się efekt uboczny.
/// </summary>
public sealed class TicketCheckout
{
    private readonly Mailer _mailer;

    public TicketCheckout()
        : this(CinemaMailer.Send)
    {
    }

    public TicketCheckout(Mailer mailer)
    {
        ArgumentNullException.ThrowIfNull(mailer);
        _mailer = mailer;
    }

    public string Pay(Booking booking, string? card)
    {
        ArgumentNullException.ThrowIfNull(card);
        if (booking.Status != BookingStatus.New)
        {
            return "ERROR: status " + booking.Status;
        }
        if (CardTerminal.Charge(card, booking.Amount))
        {
            booking.MarkPaid();
            _mailer(booking.Email, "Bilety " + booking.Id + " oplacone: " + booking.Amount);
            return "OK";
        }
        _mailer(booking.Email, "Platnosc odrzucona " + booking.Id);
        return "DECLINED";
    }
}
