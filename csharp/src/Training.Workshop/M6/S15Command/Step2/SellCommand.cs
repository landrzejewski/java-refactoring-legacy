using System.Globalization;
using System.Text.RegularExpressions;

namespace Training.Workshop.M6.S15Command.Step2;

/// <summary>Krok 2: gałąź SELL jako obiekt komendy.</summary>
public sealed class SellCommand : IConsoleCommand
{
    public string Execute(string args, Till till)
    {
        var sell = args.Split(' ', 2);
        if (sell.Length < 2 || !Regex.IsMatch(sell[0], "^[0-9]+$"))
        {
            return "Blad: SELL <liczba> <tytul>";
        }
        var quantity = int.Parse(sell[0], CultureInfo.InvariantCulture);
        var price = till.PriceOf(sell[1]);
        if (price == null)
        {
            return "Blad: nieznany film " + sell[1];
        }
        var total = price.Times(quantity);
        till.Sold(quantity, total);
        return "Sprzedano " + quantity + " x " + sell[1] + " = " + total;
    }
}
