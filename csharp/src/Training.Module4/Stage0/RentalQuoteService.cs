using System.Globalization;
using Training.Module4.Model;

namespace Training.Module4.Stage0;

public sealed class RentalQuoteService
{
    private readonly Dictionary<EquipmentType, decimal> rates = new()
    {
        [EquipmentType.Drill] = 39.99m,
        [EquipmentType.Generator] = 120.00m
    };
    private readonly decimal disc = 0.10m;

    public string CreateQuote(RentalRequest r)
    {
        decimal a = Money(rates[r.EquipmentType] * r.Days);
        decimal d = r.Days >= 7
            ? Money(a * disc)
            : Money(0m);
        decimal i = r.Insurance
            ? Money(8.00m * r.Days)
            : Money(0m);
        decimal f = r.Delivery
            ? 25.00m
            : Money(0m);
        decimal n = Money(a - d + i + f);
        decimal v = Money(n * 0.23m);
        decimal t = Money(n + v);

        string q = "RENTAL QUOTE\n"
            + "Customer: "
            + r.CustomerName.Trim().ToUpperInvariant() + "\n"
            + "Equipment: " + r.EquipmentType.ToString().ToUpperInvariant() + "\n"
            + "Days: " + r.Days.ToString(CultureInfo.InvariantCulture) + "\n"
            + "Base: " + a.ToString("0.00", CultureInfo.InvariantCulture) + "\n"
            + "Discount: " + d.ToString("0.00", CultureInfo.InvariantCulture) + "\n"
            + "Insurance: " + i.ToString("0.00", CultureInfo.InvariantCulture) + "\n"
            + "Delivery: " + f.ToString("0.00", CultureInfo.InvariantCulture) + "\n"
            + "Net: " + n.ToString("0.00", CultureInfo.InvariantCulture) + "\n"
            + "VAT: " + v.ToString("0.00", CultureInfo.InvariantCulture) + "\n"
            + "Total: " + t.ToString("0.00", CultureInfo.InvariantCulture) + "\n";
        return q;
    }

    private static decimal Money(decimal amount)
    {
        return Math.Round(amount, 2, MidpointRounding.AwayFromZero);
    }
}
