using Training.Workshop.Shared;

namespace Training.Workshop.M5.S28Chain.Step2;

/// <summary>
/// Krok 2: każda gałąź to ogniwo, a łańcuch if zastąpiła lista przeglądana po kolei.
/// Kolejność reguł jest już nazwana i widoczna w jednym miejscu. Koniec listy bez decyzji
/// to nadal jawny przypadek "brak uprawnionego zatwierdzającego".
/// </summary>
public sealed class RefundApprover
{
    private static readonly Money CashierLimit = Money.Of("100.00");
    private static readonly Money ManagerLimit = Money.Of("1000.00");

    private readonly IReadOnlyList<IRefundHandler> _handlers =
    [
        new AfterScreeningHandler(),
        new AutoRefundHandler(),
        new CashierHandler(CashierLimit),
        new ManagerHandler(ManagerLimit),
    ];

    public RefundDecision Decide(RefundRequest request)
    {
        if (!request.Amount.IsGreaterThan(Money.Zero))
        {
            throw new ArgumentException("amount must be positive");
        }
        foreach (var handler in _handlers)
        {
            var decision = handler.Handle(request);
            if (decision != null)
            {
                return decision;
            }
        }
        return RefundDecision.Rejected("brak uprawnionego zatwierdzajacego");
    }
}
