using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Start;

/// <summary>
/// Start: osobna obsługa jednego biletu i listy biletów. Reguła zwrotu (100% / 50% / 0)
/// jest napisana dwa razy - trochę inaczej, więc łatwo o rozjazd przy następnej zmianie.
/// </summary>
public sealed class RefundService
{
    private static readonly Money Fee = Money.Of("3.00");

    public Money Refund(TicketData ticket, DateTime now)
    {
        Money amount;
        if (now >= ticket.ShowStart)
        {
            amount = Money.Zero;
        }
        else if ((ticket.ShowStart - now).TotalHours >= 24)
        {
            amount = ticket.Price;
        }
        else
        {
            amount = ticket.Price.Percent(50);
        }
        return amount.Minus(Fee).Max(Money.Zero);
    }

    public Money RefundAll(IReadOnlyList<TicketData> tickets, DateTime now)
    {
        var total = Money.Zero;
        foreach (var ticket in tickets)
        {
            if (now < ticket.ShowStart)
            {
                var hours = (long)(ticket.ShowStart - now).TotalHours;
                total = total.Plus(hours >= 24 ? ticket.Price : ticket.Price.Percent(50));
            }
        }
        return total.Minus(Fee).Max(Money.Zero);
    }
}
