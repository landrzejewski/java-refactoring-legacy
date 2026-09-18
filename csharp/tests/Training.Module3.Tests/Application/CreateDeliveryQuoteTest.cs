using Training.Module3.Application;
using Training.Module3.Domain;
using Command = Training.Module3.Application.CreateDeliveryQuote.Command;

namespace Training.Module3.Tests.Application;

public sealed class CreateDeliveryQuoteTest
{
    [Fact]
    public void CalculatesStoresAndNotifiesAboutQuote()
    {
        List<DeliveryQuote> savedQuotes = [];
        List<DeliveryQuote> notifications = [];
        CreateDeliveryQuote useCase = new(
            Calculator(),
            new DelegatingQuoteRepository(savedQuotes.Add),
            new DelegatingQuoteNotifier(notifications.Add));

        DeliveryQuote result = useCase.Execute(new Command(
            "developer@example.com",
            ShippingMethod.Standard,
            new Parcel(3.00m)));

        Assert.Equal(17.28m, result.Price);
        Assert.Equal([result], savedQuotes);
        Assert.Equal([result], notifications);
    }

    [Fact]
    public void DoesNotNotifyWhenSavingFails()
    {
        List<DeliveryQuote> notifications = [];
        IQuoteRepository failingRepository = new DelegatingQuoteRepository(
            _ => throw new InvalidOperationException("Storage unavailable"));
        CreateDeliveryQuote useCase = new(
            Calculator(),
            failingRepository,
            new DelegatingQuoteNotifier(notifications.Add));

        Assert.Throws<InvalidOperationException>(
            () => useCase.Execute(new Command(
                "developer@example.com",
                ShippingMethod.Standard,
                new Parcel(3.00m))));
        Assert.Empty(notifications);
    }

    private static DeliveryPriceCalculator Calculator()
    {
        FuelSurcharge surcharge = new(0.08m);
        return new DeliveryPriceCalculator(
            [new StandardDeliveryPricePolicy(surcharge)]);
    }

    // C# lambdas cannot implement interfaces, so these adapters play the role
    // of Java's "savedQuotes::add" / "quote -> { throw ... }" lambdas.
    private sealed class DelegatingQuoteRepository(Action<DeliveryQuote> save) : IQuoteRepository
    {
        public void Save(DeliveryQuote quote) => save(quote);
    }

    private sealed class DelegatingQuoteNotifier(Action<DeliveryQuote> quoteCreated) : IQuoteNotifier
    {
        public void QuoteCreated(DeliveryQuote quote) => quoteCreated(quote);
    }
}
