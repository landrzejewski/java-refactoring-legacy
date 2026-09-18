using Training.Module3.Adapter;
using Training.Module3.Domain;

namespace Training.Module3.Tests.Adapter;

public sealed class InMemoryQuoteRepositoryTest
{
    [Fact]
    public void StoresQuote()
    {
        InMemoryQuoteRepository repository = new();
        DeliveryQuote quote = new(
            "developer@example.com",
            ShippingMethod.Standard,
            new Parcel(3.00m),
            17.28m);

        repository.Save(quote);

        Assert.Equal([quote], repository.Quotes());
    }
}
