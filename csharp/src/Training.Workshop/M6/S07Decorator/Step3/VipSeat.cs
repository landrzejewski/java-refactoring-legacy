using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step3;

/// <summary>
/// Krok 3: dopłata za miejsce VIP jako dekorator.
/// </summary>
public sealed record VipSeat(IPricedTicket Inner) : IPricedTicket
{
    public Money Price()
    {
        return Inner.Price().Plus(Money.Of("10.00"));
    }

    public string Description()
    {
        return Inner.Description() + " +VIP";
    }
}
