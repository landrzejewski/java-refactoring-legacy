using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Step2;

/// <summary>
/// Krok 2 (bez zmian): nowa ścieżka zależy od portu IEffects.
/// W cieniu dostaje nagrywarkę, w produkcji prawdziwy adapter.
/// </summary>
public sealed class NewBookingFlow
{
    private static readonly Money Ticket2D = Money.Of("25.00");
    private static readonly Money OnlineFee = Money.Of("2.00");

    private readonly IEffects _effects;

    public NewBookingFlow(IEffects effects)
    {
        _effects = effects;
    }

    public string Book(BookingRequest request)
    {
        Money tickets = Ticket2D.Times(request.Tickets);
        Money total = tickets.Plus(OnlineFee.Times(request.Tickets));
        _effects.Charge(request.Card, total);
        _effects.Save(request.Title + ";" + request.Email + ";" + request.Tickets + ";" + total);
        _effects.SendMail(request.Email,
            "Bilety " + request.Title + " x" + request.Tickets + ", zaplacono " + tickets);
        return "OK " + total;
    }
}
