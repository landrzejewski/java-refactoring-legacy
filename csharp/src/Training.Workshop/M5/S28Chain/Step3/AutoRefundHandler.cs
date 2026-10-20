namespace Training.Workshop.M5.S28Chain.Step3;

/// <summary>Krok 3: co najmniej 24 h przed seansem zatwierdza (bez limitu kwoty) i kończy łańcuch.</summary>
public sealed class AutoRefundHandler : IRefundHandler
{
    private readonly IRefundHandler _next;

    public AutoRefundHandler(IRefundHandler next)
    {
        ArgumentNullException.ThrowIfNull(next);
        _next = next;
    }

    public RefundDecision Handle(RefundRequest request)
    {
        if ((request.ScreeningStart - request.RequestedAt).TotalHours >= 24)
        {
            return RefundDecision.ApprovedBy("AUTO");
        }
        return _next.Handle(request);
    }
}
