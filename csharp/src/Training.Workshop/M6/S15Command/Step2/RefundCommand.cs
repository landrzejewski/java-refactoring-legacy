namespace Training.Workshop.M6.S15Command.Step2;

/// <summary>Krok 2: gałąź REFUND jako obiekt komendy.</summary>
public sealed class RefundCommand : IConsoleCommand
{
    public string Execute(string args, Till till)
    {
        var price = till.PriceOf(args);
        if (price == null)
        {
            return "Blad: nieznany film " + args;
        }
        if (till.Tickets == 0)
        {
            return "Blad: brak biletow do zwrotu";
        }
        till.Refunded(price);
        return "Zwrot 1 x " + args + " = " + price;
    }
}
