using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step2;

/// <summary>Krok 2: liść - reguła zwrotu jednego biletu (przeniesiona z TicketShare).</summary>
public sealed record SingleTicket(TicketData Ticket) : IRefundable
{
    public Money RefundableAmount(DateTime now)
    {
        if (now >= Ticket.ShowStart)
        {
            return Money.Zero;
        }
        if ((Ticket.ShowStart - now).TotalHours >= 24)
        {
            return Ticket.Price;
        }
        return Ticket.Price.Percent(50);
    }
}
