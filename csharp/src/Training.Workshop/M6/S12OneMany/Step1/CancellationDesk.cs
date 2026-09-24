using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step1;

/// <summary>Krok 1: klient bez zmian.</summary>
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
