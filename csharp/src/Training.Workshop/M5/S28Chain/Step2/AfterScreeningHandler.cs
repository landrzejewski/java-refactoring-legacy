namespace Training.Workshop.M5.S28Chain.Step2;

/// <summary>Krok 2: ogniwo - po rozpoczęciu seansu zwrotu nie ma.</summary>
public sealed class AfterScreeningHandler : IRefundHandler
{
    public RefundDecision? Handle(RefundRequest request)
    {
        if (!(request.RequestedAt < request.ScreeningStart))
        {
            return RefundDecision.Rejected("seans juz sie rozpoczal");
        }
        return null;
    }
}
