using Training.Module3.Domain;
using Training.Module3.Legacy;

namespace Training.Module3.Tests.Legacy;

public sealed class LegacyDeliveryQuoteServiceCharacterizationTest
{
    [Fact]
    public void DocumentsStandardDeliveryPriceAndStorage()
    {
        LegacyDeliveryQuoteService service = new();
        Parcel parcel = new(3.00m);

        DeliveryQuote quote = service.CreateQuote(
            "developer@example.com",
            ShippingMethod.Standard,
            parcel);

        Assert.Equal(17.28m, quote.Price);
        Assert.Equal([quote], service.StoredQuotes());
    }

    [Fact]
    public void DocumentsExpressDeliveryPrice()
    {
        LegacyDeliveryQuoteService service = new();

        DeliveryQuote quote = service.CreateQuote(
            "developer@example.com",
            ShippingMethod.Express,
            new Parcel(3.00m));

        Assert.Equal(31.32m, quote.Price);
    }
}
