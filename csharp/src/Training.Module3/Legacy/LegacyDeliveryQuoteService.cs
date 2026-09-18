using System.Globalization;
using Training.Module3.Domain;

namespace Training.Module3.Legacy;

public sealed class LegacyDeliveryQuoteService
{
    private readonly List<DeliveryQuote> storedQuotes = [];

    public DeliveryQuote CreateQuote(
        string customerEmail,
        ShippingMethod method,
        Parcel parcel)
    {
        decimal price;
        switch (method)
        {
            case ShippingMethod.Standard:
            {
                decimal @base = 10.00m + parcel.WeightKg * 2.00m;
                price = Money(@base + @base * 0.08m);
                break;
            }
            case ShippingMethod.Express:
            {
                decimal @base = 20.00m + parcel.WeightKg * 3.00m;
                price = Money(@base + @base * 0.08m);
                break;
            }
            default:
                throw new ArgumentOutOfRangeException(nameof(method));
        }

        DeliveryQuote quote = new(
            customerEmail,
            method,
            parcel,
            price);
        storedQuotes.Add(quote);

        Console.WriteLine(string.Format(
            CultureInfo.InvariantCulture,
            "Quote ready for {0}: {1} costs {2}",
            customerEmail,
            method.ToString().ToUpperInvariant(),
            price));
        return quote;
    }

    public IReadOnlyList<DeliveryQuote> StoredQuotes() => [.. storedQuotes];

    private static decimal Money(decimal amount) =>
        Math.Round(amount, 2, MidpointRounding.AwayFromZero);
}
