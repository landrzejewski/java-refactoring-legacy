using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step2;

/// <summary>
/// Krok 2: Replace One/Many Distinctions with Composite - jedna metoda Refund(IRefundable).
/// Stare metody zostają jako cienkie delegacje, dopóki klienci nie przejdą na nowy kontrakt.
/// </summary>
public sealed class RefundService
{
    private static readonly Money Fee = Money.Of("3.00");

    public Money Refund(IRefundable refundable, DateTime now)
    {
        return refundable.RefundableAmount(now).Minus(Fee).Max(Money.Zero);
    }

    /// <summary>Przestarzałe: użyj <see cref="Refund(IRefundable, DateTime)"/> z <see cref="SingleTicket"/>.</summary>
    [Obsolete("użyj Refund(IRefundable, DateTime) z SingleTicket")]
    public Money Refund(TicketData ticket, DateTime now)
    {
        return Refund(new SingleTicket(ticket), now);
    }

    /// <summary>Przestarzałe: użyj <see cref="Refund(IRefundable, DateTime)"/> z <see cref="TicketGroup"/>.</summary>
    [Obsolete("użyj Refund(IRefundable, DateTime) z TicketGroup")]
    public Money RefundAll(IReadOnlyList<TicketData> tickets, DateTime now)
    {
        return Refund(TicketGroup.Of(tickets), now);
    }
}
