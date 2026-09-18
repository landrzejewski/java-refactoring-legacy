using Training.Module4.Model;
using Training.Module4.Stage0;

namespace Training.Module4.Tests.Stage0;

public sealed class RentalQuoteServiceCharacterizationTest
{
    private readonly RentalQuoteService _service = new();

    [Fact]
    public void DocumentsCompleteGeneratorQuote()
    {
        var request = new RentalRequest(
            " Acme ",
            EquipmentType.Generator,
            8,
            true,
            true);

        string quote = _service.CreateQuote(request);

        Assert.Equal(
            "RENTAL QUOTE\n"
            + "Customer: ACME\n"
            + "Equipment: GENERATOR\n"
            + "Days: 8\n"
            + "Base: 960.00\n"
            + "Discount: 96.00\n"
            + "Insurance: 64.00\n"
            + "Delivery: 25.00\n"
            + "Net: 953.00\n"
            + "VAT: 219.19\n"
            + "Total: 1172.19\n",
            quote);
    }

    [Fact]
    public void DocumentsDiscountBoundary()
    {
        string sixDays = _service.CreateQuote(new RentalRequest(
            "Acme", EquipmentType.Generator, 6, false, false));
        string sevenDays = _service.CreateQuote(new RentalRequest(
            "Acme", EquipmentType.Generator, 7, false, false));

        Assert.Contains("Discount: 0.00\n", sixDays);
        Assert.Contains("Total: 885.60\n", sixDays);
        Assert.Contains("Discount: 84.00\n", sevenDays);
        Assert.Contains("Total: 929.88\n", sevenDays);
    }

    [Fact]
    public void DocumentsVatRounding()
    {
        string quote = _service.CreateQuote(new RentalRequest(
            "Acme", EquipmentType.Drill, 1, false, false));

        Assert.Contains("VAT: 9.20\n", quote);
        Assert.Contains("Total: 49.19\n", quote);
    }

    [Fact]
    public void DistinguishesInsuranceFromDelivery()
    {
        string insuranceOnly = _service.CreateQuote(new RentalRequest(
            "Acme", EquipmentType.Drill, 2, true, false));
        string deliveryOnly = _service.CreateQuote(new RentalRequest(
            "Acme", EquipmentType.Drill, 2, false, true));

        Assert.Multiple(
            () => Assert.Contains("Insurance: 16.00\n", insuranceOnly),
            () => Assert.Contains("Delivery: 0.00\n", insuranceOnly),
            () => Assert.Contains("Total: 118.06\n", insuranceOnly),
            () => Assert.Contains("Insurance: 0.00\n", deliveryOnly),
            () => Assert.Contains("Delivery: 25.00\n", deliveryOnly),
            () => Assert.Contains("Total: 129.13\n", deliveryOnly));
    }

    [Fact]
    public void DocumentsDiscountRounding()
    {
        string quote = _service.CreateQuote(new RentalRequest(
            "Acme", EquipmentType.Drill, 15, false, false));

        Assert.Contains("Discount: 59.99\n", quote);
        Assert.Contains("Net: 539.86\n", quote);
        Assert.Contains("Total: 664.03\n", quote);
    }
}
