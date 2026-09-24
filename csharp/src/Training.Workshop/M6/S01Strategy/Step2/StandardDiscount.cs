using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step2;

/// <summary>Krok 2: gałąź STANDARD przeniesiona do strategii. Bezstanowa - można ją współdzielić.</summary>
public sealed class StandardDiscount : IDiscountPolicy
{
    public Money Discount(Money basePrice, string ticketType)
    {
        var percent = ticketType switch
        {
            "N" => 0,
            "S" => 25,
            "E" => 30,
            "C" => 40,
            _ => throw new ArgumentException("unknown ticket type: " + ticketType),
        };
        return basePrice.Percent(percent);
    }
}
