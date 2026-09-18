using System.Globalization;
using Training.Module4.Model;

namespace Training.Module4.Tests;

[Collection(GlobalStateCollection.Name)]
public sealed class LocaleIndependentFormattingTest
{
    [Fact]
    public void QuoteFormattingDoesNotDependOnDefaultFormatLocale()
    {
        var originalCulture = CultureInfo.CurrentCulture;
        try
        {
            CultureInfo.CurrentCulture = CultureInfo.GetCultureInfo("ar-EG");

            // Guard: the culture must really be hostile, otherwise the test proves nothing.
            // ar-EG uses the Arabic decimal separator, so culture-sensitive formatting
            // of money would not produce "960.00".
            Assert.NotEqual("960.00", 960.00m.ToString("0.00"));

            var request = new RentalRequest(
                "Acme",
                EquipmentType.Generator,
                8,
                true,
                true);

            string approvedQuote = new Training.Module4.Stage0.RentalQuoteService()
                .CreateQuote(request);
            var stage1 = new Training.Module4.Stage1.RentalQuoteService();
            var stage2 = new Training.Module4.Stage2.RentalQuoteService();
            var stage3 = new Training.Module4.Stage3.RentalQuoteService();

            Assert.Contains("Days: 8\n", approvedQuote);
            Assert.Contains("Base: 960.00\n", approvedQuote);
            Assert.Contains("Total: 1172.19\n", approvedQuote);
            Assert.Multiple(
                () => Assert.Equal(approvedQuote, stage1.CreateQuote(request)),
                () => Assert.Equal(approvedQuote, stage2.CreateQuote(request)),
                () => Assert.Equal(approvedQuote, stage3.CreateQuote(request)));
        }
        finally
        {
            CultureInfo.CurrentCulture = originalCulture;
        }
    }
}
