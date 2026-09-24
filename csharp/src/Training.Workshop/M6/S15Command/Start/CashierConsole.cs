using System.Globalization;
using System.Text.RegularExpressions;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S15Command.Start;

/// <summary>
/// Start: konsola kasjera jako dyspozytor warunkowy - łańcuch if po nazwie komendy, a w każdej
/// gałęzi pełna logika i modyfikacja stanu kasy. Nowa komenda = kolejny else if.
/// </summary>
public sealed class CashierConsole
{
    private static readonly IReadOnlyDictionary<string, Money> Prices = new Dictionary<string, Money>
    {
        ["Diuna"] = Money.Of("40.00"),
        ["Kraina Lodu"] = Money.Of("32.00"),
        ["Amator"] = Money.Of("25.00"),
    };

    private Money _cash = Money.Zero;
    private int _tickets;

    public string Handle(string line)
    {
        var parts = line.Trim().Split(' ', 2);
        var command = parts[0].ToUpperInvariant();
        var args = parts.Length > 1 ? parts[1] : "";
        if (command == "SELL")
        {
            var sell = args.Split(' ', 2);
            if (sell.Length < 2 || !Regex.IsMatch(sell[0], "^[0-9]+$"))
            {
                return "Blad: SELL <liczba> <tytul>";
            }
            var quantity = int.Parse(sell[0], CultureInfo.InvariantCulture);
            if (!Prices.TryGetValue(sell[1], out var price))
            {
                return "Blad: nieznany film " + sell[1];
            }
            var total = price.Times(quantity);
            _cash = _cash.Plus(total);
            _tickets += quantity;
            return "Sprzedano " + quantity + " x " + sell[1] + " = " + total;
        }
        else if (command == "REFUND")
        {
            if (!Prices.TryGetValue(args, out var price))
            {
                return "Blad: nieznany film " + args;
            }
            if (_tickets == 0)
            {
                return "Blad: brak biletow do zwrotu";
            }
            _cash = _cash.Minus(price);
            _tickets--;
            return "Zwrot 1 x " + args + " = " + price;
        }
        else if (command == "REPORT")
        {
            return "Kasa: " + _cash + ", biletow: " + _tickets;
        }
        return "Nieznana komenda: " + parts[0];
    }
}
