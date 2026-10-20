using Training.Workshop.Shared;

namespace Training.Workshop.M5.S28Chain.Step3;

/// <summary>
/// Krok 3: jawne składanie łańcucha. Domyślny konstruktor buduje standardową ścieżkę, drugi
/// przyjmuje dowolną głowę łańcucha (np. festiwal bez zwrotów automatycznych). Walidacja
/// kwoty zostaje tutaj: to warunek wstępny, a nie poziom zatwierdzania.
/// </summary>
public sealed class RefundApprover
{
    private static readonly Money CashierLimit = Money.Of("100.00");
    private static readonly Money ManagerLimit = Money.Of("1000.00");

    private readonly IRefundHandler _chain;

    public RefundApprover()
        : this(new AfterScreeningHandler(
            new AutoRefundHandler(
                new CashierHandler(CashierLimit,
                    new ManagerHandler(ManagerLimit,
                        new NoApproverHandler())))))
    {
    }

    public RefundApprover(IRefundHandler chain)
    {
        ArgumentNullException.ThrowIfNull(chain);
        _chain = chain;
    }

    public RefundDecision Decide(RefundRequest request)
    {
        if (!request.Amount.IsGreaterThan(Money.Zero))
        {
            throw new ArgumentException("amount must be positive");
        }
        return _chain.Handle(request);
    }
}
