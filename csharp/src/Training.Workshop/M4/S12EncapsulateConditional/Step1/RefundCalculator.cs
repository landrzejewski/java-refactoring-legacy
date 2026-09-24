using Training.Workshop.Shared;

namespace Training.Workshop.M4.S12EncapsulateConditional.Step1;

/// <summary>
/// Krok 1: Extract Method dla najmniejszego fragmentu - <c>HasFreeTicketPromo</c>.
/// Osłona <c>Promo != null &amp;&amp;</c> idzie RAZEM z testem prefiksu:
/// krótkie spięcie jest zachowaniem.
/// </summary>
public sealed class RefundCalculator
{
    private static readonly Money CancellationFee = Money.Of("3.00");

    public Money Refund(Booking b, DateTime now)
    {
        Money amount = Money.Zero;
        if (b.Status == "PAID" && now < b.ScreeningStart && !HasFreeTicketPromo(b))
        {
            if (!(now.AddHours(24) > b.ScreeningStart))
            {
                amount = b.Tickets;
            }
            else
            {
                amount = b.Tickets.Percent(50);
            }
        }
        return amount.Minus(CancellationFee).Max(Money.Zero);
    }

    private static bool HasFreeTicketPromo(Booking b)
    {
        return b.Promo != null && b.Promo.StartsWith("FREE", StringComparison.Ordinal);
    }
}
