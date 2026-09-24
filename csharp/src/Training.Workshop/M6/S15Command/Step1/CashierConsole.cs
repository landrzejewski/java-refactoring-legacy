using System.Globalization;
using System.Text.RegularExpressions;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S15Command.Step1;

/// <summary>Krok 1: Extract Method - ciało każdej gałęzi w osobnej metodzie o tej samej sygnaturze.</summary>
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
            return Sell(args);
        }
        else if (command == "REFUND")
        {
            return Refund(args);
        }
        else if (command == "REPORT")
        {
            return Report(args);
        }
        return "Nieznana komenda: " + parts[0];
    }

    private string Sell(string args)
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

    private string Refund(string args)
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

    private string Report(string args)
    {
        return "Kasa: " + _cash + ", biletow: " + _tickets;
    }
}
