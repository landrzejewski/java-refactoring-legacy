using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Step3;

/// <summary>Krok 3: podsystem ukryty (internal) - cennik biletu 2D wg typu (N, S, E, C).</summary>
internal sealed class PriceList
{
    private static readonly IReadOnlyDictionary<string, Money> Prices = new Dictionary<string, Money>
    {
        ["N"] = Money.Of("25.00"), ["S"] = Money.Of("18.75"), ["E"] = Money.Of("17.50"), ["C"] = Money.Of("15.00"),
    };

    internal Money Price(string ticketType)
    {
        if (!Prices.TryGetValue(ticketType, out var price))
        {
            throw new ArgumentException("unknown ticket type: " + ticketType);
        }
        return price;
    }
}
