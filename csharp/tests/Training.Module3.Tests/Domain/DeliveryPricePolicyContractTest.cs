using Training.Module3.Domain;

namespace Training.Module3.Tests.Domain;

public sealed class DeliveryPricePolicyContractTest
{
    [Theory]
    [MemberData(nameof(Policies))]
    public void EveryPolicyObeysTheSubstitutionContract(
        string description,
        ShippingMethod expectedMethod,
        IDeliveryPricePolicy policy)
    {
        _ = description;
        Parcel parcel = new(100.00m);

        ShippingMethod firstMethod = policy.Method();
        ShippingMethod secondMethod = policy.Method();
        decimal firstResult = policy.PriceFor(parcel);
        decimal secondResult = policy.PriceFor(parcel);

        Assert.Multiple(
            () => Assert.Equal(expectedMethod, firstMethod),
            () => Assert.Equal(firstMethod, secondMethod),
            () => Assert.True(firstResult >= 0m),
            () => Assert.Equal(2, firstResult.Scale),
            () => Assert.Equal(firstResult, secondResult));
    }

    public static TheoryData<string, ShippingMethod, IDeliveryPricePolicy> Policies()
    {
        FuelSurcharge surcharge = new(0.08m);
        return new()
        {
            { "standard policy", ShippingMethod.Standard, new StandardDeliveryPricePolicy(surcharge) },
            { "express policy", ShippingMethod.Express, new ExpressDeliveryPricePolicy(surcharge) },
        };
    }
}
