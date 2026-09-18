namespace Training.Module2.Tests;

public sealed class LegacyInvoiceFormatterCharacterizationTest
{
    private readonly LegacyInvoiceFormatter formatter = new();

    [Fact]
    public void DocumentsCurrentFormattingAndRounding()
    {
        IReadOnlyList<InvoiceLine> lines =
        [
            new InvoiceLine("BOOK", 2, 19.99m),
            new InvoiceLine("PEN", 1, 5.00m),
        ];

        string result = formatter.Format("  Acme  ", lines);

        Assert.Equal(
            string.Join("\n",
                "INVOICE",
                "Customer: ACME",
                "BOOK x 2 = 39.98",
                "PEN x 1 = 5.00",
                "Subtotal: 44.98",
                "Tax: 10.35",
                "Total: 55.33",
                ""),
            result);
    }

    [Fact]
    public void DocumentsCurrentFallbackForMissingCustomer()
    {
        string result = formatter.Format(null, []);

        Assert.Equal(
            string.Join("\n",
                "INVOICE",
                "Customer: UNKNOWN",
                "Subtotal: 0.00",
                "Tax: 0.00",
                "Total: 0.00",
                ""),
            result);
    }
}
