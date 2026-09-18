using Training.Module4.Model;

namespace Training.Module4.Tests;

public sealed class RentalQuoteStagesEquivalenceTest
{
    public static TheoryData<string, RentalRequest, string> ApprovedQuotes() => new()
    {
        {
            "complete generator quote",
            new RentalRequest(" Acme ", EquipmentType.Generator, 8, true, true),
            Quote("""
                RENTAL QUOTE
                Customer: ACME
                Equipment: GENERATOR
                Days: 8
                Base: 960.00
                Discount: 96.00
                Insurance: 64.00
                Delivery: 25.00
                Net: 953.00
                VAT: 219.19
                Total: 1172.19
                """)
        },
        {
            "insurance without delivery",
            new RentalRequest("Acme", EquipmentType.Drill, 2, true, false),
            Quote("""
                RENTAL QUOTE
                Customer: ACME
                Equipment: DRILL
                Days: 2
                Base: 79.98
                Discount: 0.00
                Insurance: 16.00
                Delivery: 0.00
                Net: 95.98
                VAT: 22.08
                Total: 118.06
                """)
        },
        {
            "delivery without insurance",
            new RentalRequest("Acme", EquipmentType.Drill, 2, false, true),
            Quote("""
                RENTAL QUOTE
                Customer: ACME
                Equipment: DRILL
                Days: 2
                Base: 79.98
                Discount: 0.00
                Insurance: 0.00
                Delivery: 25.00
                Net: 104.98
                VAT: 24.15
                Total: 129.13
                """)
        },
        {
            "day before discount threshold",
            new RentalRequest("Acme", EquipmentType.Generator, 6, false, false),
            Quote("""
                RENTAL QUOTE
                Customer: ACME
                Equipment: GENERATOR
                Days: 6
                Base: 720.00
                Discount: 0.00
                Insurance: 0.00
                Delivery: 0.00
                Net: 720.00
                VAT: 165.60
                Total: 885.60
                """)
        },
        {
            "discount threshold",
            new RentalRequest("Acme", EquipmentType.Generator, 7, false, false),
            Quote("""
                RENTAL QUOTE
                Customer: ACME
                Equipment: GENERATOR
                Days: 7
                Base: 840.00
                Discount: 84.00
                Insurance: 0.00
                Delivery: 0.00
                Net: 756.00
                VAT: 173.88
                Total: 929.88
                """)
        },
        {
            "discount rounding",
            new RentalRequest("Acme", EquipmentType.Drill, 15, false, false),
            Quote("""
                RENTAL QUOTE
                Customer: ACME
                Equipment: DRILL
                Days: 15
                Base: 599.85
                Discount: 59.99
                Insurance: 0.00
                Delivery: 0.00
                Net: 539.86
                VAT: 124.17
                Total: 664.03
                """)
        },
        {
            "vat rounding",
            new RentalRequest("Acme", EquipmentType.Drill, 1, false, false),
            Quote("""
                RENTAL QUOTE
                Customer: ACME
                Equipment: DRILL
                Days: 1
                Base: 39.99
                Discount: 0.00
                Insurance: 0.00
                Delivery: 0.00
                Net: 39.99
                VAT: 9.20
                Total: 49.19
                """)
        }
    };

    [Theory]
    [MemberData(nameof(ApprovedQuotes))]
    public void EveryStageProducesApprovedQuote(
        string scenario,
        RentalRequest request,
        string expectedQuote)
    {
        Assert.NotEmpty(scenario);
        var stage0 = new Training.Module4.Stage0.RentalQuoteService();
        var stage1 = new Training.Module4.Stage1.RentalQuoteService();
        var stage2 = new Training.Module4.Stage2.RentalQuoteService();
        var stage3 = new Training.Module4.Stage3.RentalQuoteService();

        Assert.Multiple(
            () => Assert.Equal(expectedQuote, stage0.CreateQuote(request)),
            () => Assert.Equal(expectedQuote, stage1.CreateQuote(request)),
            () => Assert.Equal(expectedQuote, stage2.CreateQuote(request)),
            () => Assert.Equal(expectedQuote, stage3.CreateQuote(request)));
    }

    // Java text blocks always use '\n' and end with a trailing newline.
    private static string Quote(string text) => text.ReplaceLineEndings("\n") + "\n";
}
