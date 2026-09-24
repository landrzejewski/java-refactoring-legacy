using Training.Workshop.Shared;

namespace Training.Workshop.M4.S12EncapsulateConditional.Start;

/// <summary>
/// Start: kwota zwrotu. Pierwszy if pyta o implementację (status, czas, prefiks kodu promocji),
/// a nie o regułę "czy rezerwacji przysługuje zwrot". Drugi if ukrywa próg 24 godzin.
/// </summary>
public sealed class RefundCalculator
{
    private static readonly Money CancellationFee = Money.Of("3.00");

    public Money Refund(Booking b, DateTime now)
    {
        Money amount = Money.Zero;
        if (b.Status == "PAID" && now < b.ScreeningStart
            && !(b.Promo != null && b.Promo.StartsWith("FREE", StringComparison.Ordinal)))
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
}
