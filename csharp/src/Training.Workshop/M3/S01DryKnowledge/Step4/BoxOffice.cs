namespace Training.Workshop.M3.S01DryKnowledge.Step4;

/// <summary>
/// Krok 4 (rozwiązanie): Extract Class - reguła ceny przeniesiona do <see cref="TicketPrice"/>.
/// BoxOffice zna już tylko własną wiedzę: reguły zwrotu. Zmiana zniżki = jedno miejsce.
/// </summary>
public sealed class BoxOffice
{
    private const decimal RefundDeduction = 3.00m;

    private readonly TicketPrice _ticketPrice = new();

    public decimal Sell(Ticket ticket)
    {
        return _ticketPrice.Of(ticket);
    }

    public decimal Refund(Ticket ticket, long hoursBeforeStart)
    {
        var paid = _ticketPrice.Of(ticket);
        var percent = hoursBeforeStart >= 24 ? 100 : hoursBeforeStart > 0 ? 50 : 0;
        var refund = Math.Round(paid * percent / 100, 2, MidpointRounding.AwayFromZero) - RefundDeduction;
        return Math.Round(Math.Max(refund, 0m), 2, MidpointRounding.AwayFromZero);
    }
}
