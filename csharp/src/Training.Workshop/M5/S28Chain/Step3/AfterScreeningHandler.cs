namespace Training.Workshop.M5.S28Chain.Step3;

/// <summary>Krok 3: po rozpoczęciu seansu odrzuca i kończy łańcuch; wcześniej przekazuje dalej.</summary>
public sealed class AfterScreeningHandler : IRefundHandler
{
    private readonly IRefundHandler _next;

    public AfterScreeningHandler(IRefundHandler next)
    {
        ArgumentNullException.ThrowIfNull(next);
        _next = next;
    }

    public RefundDecision Handle(RefundRequest request)
    {
        if (!(request.RequestedAt < request.ScreeningStart))
        {
            return RefundDecision.Rejected("seans juz sie rozpoczal");
        }
        return _next.Handle(request);
    }
}
