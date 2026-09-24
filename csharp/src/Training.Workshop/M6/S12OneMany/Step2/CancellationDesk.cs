using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step2;

/// <summary>Krok 2: klient przeniesiony na nowy kontrakt; rozróżnienie jeden/wiele jeszcze widać.</summary>
public sealed class CancellationDesk
{
    private readonly RefundService _service = new();

    public Money Refund(IReadOnlyList<TicketData> tickets, DateTime now)
    {
        IRefundable refundable = tickets.Count == 1
            ? new SingleTicket(tickets[0])
            : TicketGroup.Of(tickets);
        return _service.Refund(refundable, now);
    }
}
