using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Start;

/// <summary>
/// Start: cennik parsowany w konstruktorze. Jest niemutowalny, więc każda instancja jest
/// równoważna - a mimo to klienci tworzą nową przy każdym wywołaniu. Licznik służy do pomiaru.
/// </summary>
public sealed class PriceList
{
    private const string Tariff = "2D=25.00;3D=32.00;IMAX=40.00";
    private static int _created;

    private readonly Dictionary<string, Money> _prices = [];

    public PriceList()
    {
        foreach (var entry in Tariff.Split(';'))
        {
            var pair = entry.Split('=');
            _prices[pair[0]] = Money.Of(pair[1]);
        }
        Interlocked.Increment(ref _created);
    }

    public static int Created()
    {
        return Volatile.Read(ref _created);
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
