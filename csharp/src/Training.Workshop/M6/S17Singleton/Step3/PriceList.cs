using System.Collections.Frozen;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Step3;

/// <summary>
/// Krok 3: singleton bez zmian, ale implementuje ITariff - to domyślna, nie jedyna
/// implementacja. Cykl życia (jedna instancja) to decyzja korzenia kompozycji.
/// </summary>
public sealed class PriceList : ITariff
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
