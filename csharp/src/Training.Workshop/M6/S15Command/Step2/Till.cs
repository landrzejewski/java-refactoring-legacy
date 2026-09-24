using Training.Workshop.Shared;

namespace Training.Workshop.M6.S15Command.Step2;

/// <summary>Krok 2: stan kasy wydzielony z konsoli - komendy dostają go jako argument i same są bezstanowe.</summary>
public sealed class Till
{
    private static readonly IReadOnlyDictionary<string, Money> Prices = new Dictionary<string, Money>
    {
        ["Diuna"] = Money.Of("40.00"),
        ["Kraina Lodu"] = Money.Of("32.00"),
        ["Amator"] = Money.Of("25.00"),
    };

    internal Money Cash { get; private set; } = Money.Zero;

    internal int Tickets { get; private set; }

    internal Money? PriceOf(string title)
    {
        return Prices.GetValueOrDefault(title);
    }

    internal void Sold(int quantity, Money total)
    {
        Cash = Cash.Plus(total);
        Tickets += quantity;
    }

    internal void Refunded(Money price)
    {
        Cash = Cash.Minus(price);
        Tickets--;
    }
}
