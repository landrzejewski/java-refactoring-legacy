using Training.Workshop.Shared;

namespace Training.Workshop.M5.S28Chain.Step1;

/// <summary>
/// Krok 1: Extract Interface + Extract Class dla pierwszej gałęzi. Reszta łańcucha if bez zmian;
/// walidacja kwoty zostaje w approverze - to warunek wstępny, nie poziom zatwierdzania.
/// </summary>
public sealed class RefundApprover
{
    private static readonly Money CashierLimit = Money.Of("100.00");
    private static readonly Money ManagerLimit = Money.Of("1000.00");

    private readonly IRefundHandler _afterScreening = new AfterScreeningHandler();

    public RefundDecision Decide(RefundRequest request)
    {
        if (!request.Amount.IsGreaterThan(Money.Zero))
        {
            throw new ArgumentException("amount must be positive");
        }
        var decision = _afterScreening.Handle(request);
        if (decision != null)
        {
            return decision;
        }
        else if ((request.ScreeningStart - request.RequestedAt).TotalHours >= 24)
        {
            return RefundDecision.ApprovedBy("AUTO");
        }
        else if (!request.Amount.IsGreaterThan(CashierLimit))
        {
            return RefundDecision.ApprovedBy("CASHIER");
        }
        else if (!request.Amount.IsGreaterThan(ManagerLimit))
        {
            return RefundDecision.ApprovedBy("MANAGER");
        }
        return RefundDecision.Rejected("brak uprawnionego zatwierdzajacego");
    }
}
