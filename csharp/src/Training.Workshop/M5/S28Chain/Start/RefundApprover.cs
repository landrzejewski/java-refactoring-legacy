using Training.Workshop.Shared;

namespace Training.Workshop.M5.S28Chain.Start;

/// <summary>
/// Start: kto zatwierdza zwrot, rozstrzyga łańcuch if/else. Kolejność warunków jest regułą
/// biznesową (np. zwrot automatyczny nie ma limitu kwoty, bo stoi przed kasjerem), ale nie ma
/// nazwy - zmiana kolejności, dodanie poziomu albo innej ścieżki dla festiwalu oznacza edycję
/// tej jednej metody. Gdy nikt nie jest uprawniony, prośba "spada" z końca łańcucha.
/// </summary>
public sealed class RefundApprover
{
    private static readonly Money CashierLimit = Money.Of("100.00");
    private static readonly Money ManagerLimit = Money.Of("1000.00");

    public RefundDecision Decide(RefundRequest request)
    {
        if (!request.Amount.IsGreaterThan(Money.Zero))
        {
            throw new ArgumentException("amount must be positive");
        }
        if (!(request.RequestedAt < request.ScreeningStart))
        {
            return RefundDecision.Rejected("seans juz sie rozpoczal");
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
