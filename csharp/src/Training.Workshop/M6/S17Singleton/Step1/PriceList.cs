using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Step1;

/// <summary>
/// Krok 1: Limit Instantiation with Singleton (forma klasyczna) - prywatny konstruktor,
/// jedna instancja tworzona przy inicjalizacji klasy. Bezpieczne, bo obiekt jest niemutowalny.
/// </summary>
public sealed class PriceList
{
    private const string Tariff = "2D=25.00;3D=32.00;IMAX=40.00";
    private static readonly PriceList Instance = new();

    private readonly Dictionary<string, Money> _prices = [];

    private PriceList()
    {
        foreach (var entry in Tariff.Split(';'))
        {
            var pair = entry.Split('=');
            _prices[pair[0]] = Money.Of(pair[1]);
        }
    }

    public static PriceList GetInstance()
    {
        return Instance;
    }

    public Money BasePrice(string format)
    {
        if (!_prices.TryGetValue(format, out var price))
        {
            throw new ArgumentException("unknown format: " + format);
        }
        return price;
    }
}
