namespace Training.Workshop.M5.S28Chain.Step3;

/// <summary>Krok 3: jawny koniec łańcucha - żądanie nie może po cichu "spaść" bez odpowiedzi.</summary>
public sealed class NoApproverHandler : IRefundHandler
{
    public RefundDecision Handle(RefundRequest request)
    {
        return RefundDecision.Rejected("brak uprawnionego zatwierdzajacego");
    }
}
