using Training.Workshop.Shared;

namespace Training.Workshop.M5.S28Chain.Step2;

/// <summary>Krok 2: poziom CASHIER zatwierdza zwroty do swojego limitu (włącznie).</summary>
public sealed class CashierHandler : IRefundHandler
{
    private readonly Money _limit;

    public CashierHandler(Money limit)
    {
        ArgumentNullException.ThrowIfNull(limit);
        _limit = limit;
    }

    public RefundDecision? Handle(RefundRequest request)
    {
        if (!request.Amount.IsGreaterThan(_limit))
        {
            return RefundDecision.ApprovedBy("CASHIER");
        }
        return null;
    }
}
