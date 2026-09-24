using Training.Workshop.Shared;

namespace Training.Workshop.M4.S12EncapsulateConditional.Step2;

/// <summary>
/// Krok 2: Encapsulate Conditional - cały warunek jako <c>IsRefundable(b, now)</c>.
/// if pyta teraz o regułę biznesową; szczegóły (status, czas, promocja) są w jednym miejscu.
/// </summary>
public sealed class RefundCalculator
{
    private static readonly Money CancellationFee = Money.Of("3.00");

    public Money Refund(Booking b, DateTime now)
    {
        Money amount = Money.Zero;
        if (IsRefundable(b, now))
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

    private static bool IsRefundable(Booking b, DateTime now)
    {
        return b.Status == "PAID" && now < b.ScreeningStart && !HasFreeTicketPromo(b);
    }

    private static bool HasFreeTicketPromo(Booking b)
    {
        return b.Promo != null && b.Promo.StartsWith("FREE", StringComparison.Ordinal);
    }
}
