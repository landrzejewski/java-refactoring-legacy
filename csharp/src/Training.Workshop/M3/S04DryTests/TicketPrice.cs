namespace Training.Workshop.M3.S04DryTests;

/// <summary>Kod produkcyjny sceny (stabilny): cena biletu według taryfy, minus 5.00 za seans poranny.</summary>
public sealed class TicketPrice
{
    public TicketPrice(Tariff tariff)
    {
        Tariff = tariff;
    }

    public Tariff Tariff { get; }

    public decimal Of(string format, string type, TimeOnly start)
    {
        var @base = Tariff.BasePrices[format];
        var discount = Math.Round(@base * Tariff.DiscountPercents[type] / 100, 2, MidpointRounding.AwayFromZero);
        var price = @base - discount;
        if (start.Hour < 12)
        {
            price -= 5.00m;
        }
        return Math.Round(price, 2, MidpointRounding.AwayFromZero);
    }
}
