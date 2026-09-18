using Training.Module4.Encapsulation;
using Training.Module4.Model;
using Training.Module4.Pricing;

namespace Training.Module4;

public static class Module4Examples
{
    public static void Main(string[] args)
    {
        var request = new RentalRequest(
            " Acme ",
            EquipmentType.Generator,
            8,
            true,
            true);

        string legacyQuote = new Stage0.RentalQuoteService()
            .CreateQuote(request);
        string refactoredQuote = new Stage3.RentalQuoteService(
                RentalPricing.Standard())
            .CreateQuote(request);

        if (legacyQuote != refactoredQuote)
        {
            throw new InvalidOperationException("Refactoring changed the quote");
        }

        var sourceRates = new Dictionary<EquipmentType, decimal>
        {
            [EquipmentType.Drill] = 39.99m
        };
        var catalog = new EquipmentCatalog("Summer rental", sourceRates);
        var snapshot = catalog.DailyRates();
        catalog.ChangeDailyRate(EquipmentType.Drill, 42.00m);

        Console.Write(refactoredQuote);
        Console.WriteLine("Quote stages equivalent: true");
        Console.WriteLine(
            "Catalog snapshot isolated: "
            + JavaBoolean(snapshot[EquipmentType.Drill] == 39.99m));
    }

    // Java prints booleans as "true"/"false"; C# bool.ToString() yields "True"/"False".
    private static string JavaBoolean(bool value) => value ? "true" : "false";
}
