using Training.Module4.Model;
using Training.Module4.Pricing;

namespace Training.Module4.Tests.Pricing;

public sealed class RentalPricingTest
{
    [Fact]
    public void CalculatesApprovedPriceBreakdown()
    {
        var request = new RentalRequest(
            "Acme",
            EquipmentType.Generator,
            8,
            true,
            true);

        var price = RentalPricing.Standard().Calculate(request);

        Assert.Equal(
            new PriceBreakdown(
                960.00m,
                96.00m,
                64.00m,
                25.00m,
                953.00m,
                219.19m,
                1172.19m),
            price);
    }

    [Fact]
    public void RejectsIncompleteRateConfiguration()
    {
        var incompleteRates = new Dictionary<EquipmentType, decimal>
        {
            [EquipmentType.Drill] = 39.99m
        };

        Assert.Throws<ArgumentException>(
            () => new RentalPricing(incompleteRates, 0.10m));
    }

    [Fact]
    public void RejectsNonPositiveDailyRate()
    {
        var rates = CompleteRates();
        rates[EquipmentType.Drill] = 0m;

        Assert.Throws<ArgumentException>(() => new RentalPricing(rates, 0.10m));
    }

    [Fact]
    public void RejectsNegativeDailyRate()
    {
        var rates = CompleteRates();
        rates[EquipmentType.Drill] = -1.00m;

        Assert.Throws<ArgumentException>(() => new RentalPricing(rates, 0.10m));
    }

    [Fact]
    public void RejectsDailyRateThatRoundsToZero()
    {
        var rates = CompleteRates();
        rates[EquipmentType.Drill] = 0.004m;

        Assert.Throws<ArgumentException>(() => new RentalPricing(rates, 0.10m));
    }

    [Fact]
    public void RejectsDiscountOutsideClosedUnitInterval()
    {
        var rates = CompleteRates();

        Assert.Multiple(
            () => Assert.Throws<ArgumentException>(() => new RentalPricing(rates, -0.01m)),
            () => Assert.Throws<ArgumentException>(() => new RentalPricing(rates, 1.01m)));
    }

    [Fact]
    public void OwnsDefensiveCopyOfDailyRates()
    {
        var rates = CompleteRates();
        var pricing = new RentalPricing(rates, 0.10m);

        rates[EquipmentType.Drill] = 1.00m;
        var price = pricing.Calculate(new RentalRequest(
            "Acme",
            EquipmentType.Drill,
            1,
            false,
            false));

        Assert.Equal(39.99m, price.BaseRentalCost);
    }

    private static Dictionary<EquipmentType, decimal> CompleteRates()
    {
        return new Dictionary<EquipmentType, decimal>
        {
            [EquipmentType.Drill] = 39.99m,
            [EquipmentType.Generator] = 120.00m
        };
    }
}
