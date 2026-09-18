using System.Globalization;

namespace Training.Module1.Tests;

public sealed class SalesCalculationsTest
{
    [Fact]
    public void DuplicatedCalculationsCurrentlyProduceTheSameResult()
    {
        decimal unitPrice = 100.00m;

        decimal invoice = SalesCalculations.InvoiceLineTotal(unitPrice, 1, true);
        decimal quote = SalesCalculations.QuoteLineTotal(unitPrice, 1, true);

        // decimal equality ignores scale, so the scale (as in BigDecimal.equals) is checked via text
        Assert.Equal("90.0000", invoice.ToString(CultureInfo.InvariantCulture));
        Assert.Equal(invoice, quote);
    }
}
