namespace Training.Workshop.M7.S15BehaviourVector.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): drugi seam - PaymentGateway. Test zapisuje maile i obciążenia
/// do JEDNEGO dziennika, więc widzi też ich kolejność. Razem ze stanem rezerwacji
/// i wyjątkami to pełny wektor obserwowalnego zachowania tej metody.
/// </summary>
public sealed class TicketCheckout
{
    private readonly Mailer _mailer;
    private readonly PaymentGateway _gateway;

    public TicketCheckout()
        : this(CinemaMailer.Send, CardTerminal.Charge)
    {
    }

    public TicketCheckout(Mailer mailer, PaymentGateway gateway)
    {
        ArgumentNullException.ThrowIfNull(mailer);
        ArgumentNullException.ThrowIfNull(gateway);
        _mailer = mailer;
        _gateway = gateway;
    }

    public string Pay(Booking booking, string? card)
    {
        ArgumentNullException.ThrowIfNull(card);
        if (booking.Status != BookingStatus.New)
        {
            return "ERROR: status " + booking.Status;
        }
        if (_gateway(card, booking.Amount))
        {
            booking.MarkPaid();
            _mailer(booking.Email, "Bilety " + booking.Id + " oplacone: " + booking.Amount);
            return "OK";
        }
        _mailer(booking.Email, "Platnosc odrzucona " + booking.Id);
        return "DECLINED";
    }
}
