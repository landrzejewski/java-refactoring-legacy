using Training.Workshop.Shared;

namespace Training.Workshop.M4.S12EncapsulateConditional.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Encapsulate Conditional dla gałęzi - <c>CancelledAtLeast24hBefore</c>.
/// Podwójne zaprzeczenie <c>!(now.AddHours(24) &gt; ...)</c> zamknięte w nazwie z regulaminu.
/// </summary>
public sealed class RefundCalculator
{
    private static readonly Money CancellationFee = Money.Of("3.00");

    public Money Refund(Booking b, DateTime now)
    {
        Money amount = Money.Zero;
        if (IsRefundable(b, now))
        {
            if (CancelledAtLeast24hBefore(b, now))
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

    private static bool CancelledAtLeast24hBefore(Booking b, DateTime now)
    {
        return !(now.AddHours(24) > b.ScreeningStart);
    }
}
