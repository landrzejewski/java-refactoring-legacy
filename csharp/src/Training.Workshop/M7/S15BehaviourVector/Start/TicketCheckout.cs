namespace Training.Workshop.M7.S15BehaviourVector.Start;

/// <summary>
/// Start: kod po "porządkach" kolegi (Consolidate Duplicate Conditional Fragments) -
/// mail potwierdzający wysunięto za if, więc dostaje go także klient z odrzuconą kartą.
/// Test sprawdza tylko wynik (OK / DECLINED) i jest zielony. Regresja przeszła.
/// </summary>
public sealed class TicketCheckout
{
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
            CinemaMailer.Send(booking.Email, "Platnosc odrzucona " + booking.Id);
        }
        CinemaMailer.Send(booking.Email, "Bilety " + booking.Id + " oplacone: " + booking.Amount);
        return charged ? "OK" : "DECLINED";
    }
}
