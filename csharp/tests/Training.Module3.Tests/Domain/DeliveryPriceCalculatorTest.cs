using Training.Module3.Domain;

namespace Training.Module3.Tests.Domain;

public sealed class DeliveryPriceCalculatorTest
{
    [Theory]
    [MemberData(nameof(DeliveryPrices))]
    public void DelegatesToPolicySelectedByShippingMethod(
        ShippingMethod method,
        decimal expectedPrice)
    {
        DeliveryPriceCalculator calculator = CalculatorWithAllPolicies();

        decimal price = calculator.PriceFor(
            method,
            new Parcel(3.00m));

        Assert.Equal(expectedPrice, price);
    }

    [Fact]
    public void RejectsDuplicatePolicyForOneShippingMethod()
    {
        FuelSurcharge surcharge = FuelSurcharge();

        Assert.Throws<ArgumentException>(
            () => new DeliveryPriceCalculator(
            [
                new StandardDeliveryPricePolicy(surcharge),
                new StandardDeliveryPricePolicy(surcharge),
            ]));
    }

    [Fact]
    public void ReportsMissingPolicy()
    {
        DeliveryPriceCalculator calculator = new(
            [new StandardDeliveryPricePolicy(FuelSurcharge())]);

        Assert.Throws<ArgumentException>(
            () => calculator.PriceFor(
                ShippingMethod.Express,
                new Parcel(3.00m)));
    }

    public static TheoryData<ShippingMethod, decimal> DeliveryPrices() => new()
    {
        { ShippingMethod.Standard, 17.28m },
        { ShippingMethod.Express, 31.32m },
    };

    private static DeliveryPriceCalculator CalculatorWithAllPolicies()
    {
        FuelSurcharge surcharge = FuelSurcharge();
        return new DeliveryPriceCalculator(
        [
            new StandardDeliveryPricePolicy(surcharge),
            new ExpressDeliveryPricePolicy(surcharge),
        ]);
    }

    private static FuelSurcharge FuelSurcharge() => new(0.08m);
}
