namespace Training.Workshop.M5.S28Chain.Step2;

/// <summary>Krok 2: co najmniej 24 h przed seansem zwrot jest automatyczny - bez limitu kwoty.</summary>
public sealed class AutoRefundHandler : IRefundHandler
{
    public RefundDecision? Handle(RefundRequest request)
    {
        if ((request.ScreeningStart - request.RequestedAt).TotalHours >= 24)
        {
            return RefundDecision.ApprovedBy("AUTO");
        }
        return null;
    }
}
