using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Start;

/// <summary>Start: klient - kasa zwrotów. Sam rozróżnia jeden bilet i wiele biletów.</summary>
public sealed class CancellationDesk
{
    private readonly RefundService _service = new();

    public Money Refund(IReadOnlyList<TicketData> tickets, DateTime now)
    {
        if (tickets.Count == 1)
        {
            return _service.Refund(tickets[0], now);
        }
        return _service.RefundAll(tickets, now);
    }
}
