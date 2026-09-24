using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step2;

/// <summary>Krok 2: węzeł - suma zwrotów elementów; pusta grupa daje 0.00.</summary>
public sealed record TicketGroup : IRefundable
{
    public TicketGroup(IEnumerable<IRefundable> items)
    {
        Items = items.ToList().AsReadOnly();
    }

    public IReadOnlyList<IRefundable> Items { get; }

    public static TicketGroup Of(IEnumerable<TicketData> tickets)
    {
        return new TicketGroup(tickets.Select(ticket => (IRefundable)new SingleTicket(ticket)));
    }

    public Money RefundableAmount(DateTime now)
    {
        var total = Money.Zero;
        foreach (var item in Items)
        {
            total = total.Plus(item.RefundableAmount(now));
        }
        return total;
    }
}
