namespace Training.Workshop.M7.S15BehaviourVector.Step1;

/// <summary>
/// Krok 1: Parameterize Constructor z Mailer - maile stają się obserwowalne.
/// Kod poza tym bez zmian, więc regresja nadal tu jest - ale teraz test ją WIDZI i dokumentuje.
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
        var charged = CardTerminal.Charge(card, booking.Amount);
        if (charged)
        {
            booking.MarkPaid();
        }
        else
        {
            _mailer(booking.Email, "Platnosc odrzucona " + booking.Id);
        }
        _mailer(booking.Email, "Bilety " + booking.Id + " oplacone: " + booking.Amount);
        return charged ? "OK" : "DECLINED";
    }
}
