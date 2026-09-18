using Training.Module3.Adapter;
using Training.Module3.Application;
using Training.Module3.Domain;
using Training.Module3.Legacy;
using Command = Training.Module3.Application.CreateDeliveryQuote.Command;

namespace Training.Module3;

public static class Module3Examples
{
    public static void Main(string[] args)
    {
        Parcel parcel = new(3.00m);
        LegacyDeliveryQuoteService legacy = new();
        DeliveryQuote legacyQuote = legacy.CreateQuote(
            "developer@example.com",
            ShippingMethod.Standard,
            parcel);

        FuelSurcharge fuelSurcharge = new(0.08m);
        DeliveryPriceCalculator calculator = new(
        [
            new StandardDeliveryPricePolicy(fuelSurcharge),
            new ExpressDeliveryPricePolicy(fuelSurcharge),
        ]);
        InMemoryQuoteRepository repository = new();
        CreateDeliveryQuote useCase = new(
            calculator,
            repository,
            new ConsoleQuoteNotifier());

        DeliveryQuote refactoredQuote = useCase.Execute(new Command(
            "developer@example.com",
            ShippingMethod.Standard,
            parcel));

        Console.WriteLine(
            "Legacy and refactored prices equal: "
            + (legacyQuote.Price == refactoredQuote.Price).ToString().ToLowerInvariant());
        Console.WriteLine("Stored quotes: " + repository.Quotes().Count);
    }
}
