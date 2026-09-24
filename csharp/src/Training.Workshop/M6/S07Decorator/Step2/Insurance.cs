using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step2;

/// <summary>
/// Krok 2: pierwszy dodatek jako dekorator. Zaczynamy od ubezpieczenia, bo w opisie jest
/// ostatnie - dekorator dopisuje się na końcu opisu obiektu, który owija.
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
