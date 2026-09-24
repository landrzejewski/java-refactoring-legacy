using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step3;

/// <summary>Krok 3: po migracji klientów stare metody usunięte - został jeden kontrakt.</summary>
public sealed class RefundService
{
    private static readonly Money Fee = Money.Of("3.00");

    public Money Refund(IRefundable refundable, DateTime now)
    {
        return refundable.RefundableAmount(now).Minus(Fee).Max(Money.Zero);
    }
}
