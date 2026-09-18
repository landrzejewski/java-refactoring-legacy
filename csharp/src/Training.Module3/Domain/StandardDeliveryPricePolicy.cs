namespace Training.Module3.Domain;

public sealed class StandardDeliveryPricePolicy : IDeliveryPricePolicy
{
    private const decimal BasePrice = 10.00m;
    private const decimal PricePerKg = 2.00m;

    private readonly FuelSurcharge fuelSurcharge;

    public StandardDeliveryPricePolicy(FuelSurcharge fuelSurcharge)
    {
        ArgumentNullException.ThrowIfNull(fuelSurcharge);
        this.fuelSurcharge = fuelSurcharge;
    }

    public ShippingMethod Method() => ShippingMethod.Standard;

    public decimal PriceFor(Parcel parcel)
    {
        decimal baseAmount = BasePrice + parcel.WeightKg * PricePerKg;
        return fuelSurcharge.AddTo(baseAmount);
    }
}
