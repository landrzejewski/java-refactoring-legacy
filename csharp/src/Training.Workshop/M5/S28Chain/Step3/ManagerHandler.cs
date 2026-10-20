using Training.Workshop.Shared;

namespace Training.Workshop.M5.S28Chain.Step3;

/// <summary>Krok 3: poziom MANAGER - w limicie zatwierdza i kończy, powyżej eskaluje do następnika.</summary>
public sealed class ManagerHandler : IRefundHandler
{
    private readonly Money _limit;
    private readonly IRefundHandler _next;

    public ManagerHandler(Money limit, IRefundHandler next)
    {
        ArgumentNullException.ThrowIfNull(limit);
        ArgumentNullException.ThrowIfNull(next);
        _limit = limit;
        _next = next;
    }

    public RefundDecision Handle(RefundRequest request)
    {
        if (!request.Amount.IsGreaterThan(_limit))
        {
            return RefundDecision.ApprovedBy("MANAGER");
        }
        return _next.Handle(request);
    }
}
