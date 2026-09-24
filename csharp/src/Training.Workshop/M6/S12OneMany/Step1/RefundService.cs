using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step1;

/// <summary>Krok 1: Extract Method TicketShare - reguła zwrotu jednego biletu w jednym miejscu.</summary>
public sealed class RefundService
{
    private static readonly Money Fee = Money.Of("3.00");

    public Money Refund(TicketData ticket, DateTime now)
    {
        return TicketShare(ticket, now).Minus(Fee).Max(Money.Zero);
    }

    public Money RefundAll(IReadOnlyList<TicketData> tickets, DateTime now)
    {
        var total = Money.Zero;
        foreach (var ticket in tickets)
        {
            total = total.Plus(TicketShare(ticket, now));
        }
        return total.Minus(Fee).Max(Money.Zero);
    }

    private static Money TicketShare(TicketData ticket, DateTime now)
    {
        if (now >= ticket.ShowStart)
        {
            return Money.Zero;
        }
        if ((ticket.ShowStart - now).TotalHours >= 24)
        {
            return ticket.Price;
        }
        return ticket.Price.Percent(50);
    }
}
