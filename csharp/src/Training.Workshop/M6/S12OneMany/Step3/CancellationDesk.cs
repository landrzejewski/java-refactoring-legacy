using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step3;

/// <summary>Krok 3: rozróżnienie zniknęło - grupa jednego biletu zachowuje się jak bilet.</summary>
public sealed class CancellationDesk
{
    private readonly RefundService _service = new();

    public Money Refund(IReadOnlyList<TicketData> tickets, DateTime now)
    {
        return _service.Refund(TicketGroup.Of(tickets), now);
    }
}
