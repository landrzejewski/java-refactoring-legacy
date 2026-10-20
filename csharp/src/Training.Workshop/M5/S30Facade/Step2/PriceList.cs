using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Step2;

/// <summary>Podsystem: cennik biletu 2D wg typu (N, S, E, C).</summary>
public sealed class PriceList
{
    private static readonly IReadOnlyDictionary<string, Money> Prices = new Dictionary<string, Money>
    {
        ["N"] = Money.Of("25.00"), ["S"] = Money.Of("18.75"), ["E"] = Money.Of("17.50"), ["C"] = Money.Of("15.00"),
    };

    public Money Price(string ticketType)
    {
        if (!Prices.TryGetValue(ticketType, out var price))
        {
            throw new ArgumentException("unknown ticket type: " + ticketType);
        }
        return price;
    }
}
