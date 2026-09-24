using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step3;

/// <summary>
/// Krok 3: bez zmian - ubezpieczenie jest zawsze najbardziej zewnętrzne,
/// bo w opisie występuje na końcu.
/// </summary>
public sealed record Insurance(IPricedTicket Inner) : IPricedTicket
{
    public Money Price()
    {
        return Inner.Price().Plus(Money.Of("4.00"));
    }

    public string Description()
    {
        return Inner.Description() + " +ubezpieczenie";
    }
}
