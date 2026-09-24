using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Start;

/// <summary>
/// Start: nowa ścieżka rezerwacji. Liczy poprawnie, ale obliczenie jest splecione z efektami:
/// sama obciąża kartę, zapisuje wiersz i wysyła mail.
/// </summary>
public sealed class NewBookingFlow
{
    private static readonly Money Ticket2D = Money.Of("25.00");
    private static readonly Money OnlineFee = Money.Of("2.00");

    private readonly Infrastructure _infra;

    public NewBookingFlow(Infrastructure infra)
    {
        _infra = infra;
    }

    public string Book(BookingRequest request)
    {
        Money tickets = Ticket2D.Times(request.Tickets);
        Money total = tickets.Plus(OnlineFee.Times(request.Tickets));
        _infra.Charge(request.Card, total);
        _infra.Save(request.Title + ";" + request.Email + ";" + request.Tickets + ";" + total);
        _infra.SendMail(request.Email,
            "Bilety " + request.Title + " x" + request.Tickets + ", zaplacono " + tickets);
        return "OK " + total;
    }
}
