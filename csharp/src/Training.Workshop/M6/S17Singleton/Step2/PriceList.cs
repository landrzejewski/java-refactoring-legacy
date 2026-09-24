using System.Collections.Frozen;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Step2;

/// <summary>
/// Krok 2: singleton w formie idiomatycznej dla C# (odpowiednik enum z Javy) - statyczna właściwość
/// tylko do odczytu. CLR gwarantuje jednokrotną, bezpieczną wątkowo inicjalizację typu (na
/// AssemblyLoadContext, nie "na proces"). Mapa niemodyfikowalna po zbudowaniu.
/// </summary>
public sealed class PriceList
{
    private const string Tariff = "2D=25.00;3D=32.00;IMAX=40.00";

    private readonly FrozenDictionary<string, Money> _prices;

    private PriceList()
    {
        var parsed = new Dictionary<string, Money>();
        foreach (var entry in Tariff.Split(';'))
        {
            var pair = entry.Split('=');
            parsed[pair[0]] = Money.Of(pair[1]);
        }
        _prices = parsed.ToFrozenDictionary();
    }

    public static PriceList Instance { get; } = new();

    public Money BasePrice(string format)
    {
        if (!_prices.TryGetValue(format, out var price))
        {
            throw new ArgumentException("unknown format: " + format);
        }
        return price;
    }
}
