using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step3;

/// <summary>
/// Krok 3: okulary 3D jako dekorator; decyzja "czy potrzebne" została w fabryce.
/// </summary>
public sealed record Glasses3D(IPricedTicket Inner) : IPricedTicket
{
    public Money Price()
    {
        return Inner.Price().Plus(Money.Of("3.00"));
    }

    public string Description()
    {
        return Inner.Description() + " +okulary 3D";
    }
}
