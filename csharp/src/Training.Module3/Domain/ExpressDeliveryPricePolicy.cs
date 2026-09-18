namespace Training.Module3.Domain;

public sealed class ExpressDeliveryPricePolicy : IDeliveryPricePolicy
{
    private const decimal BasePrice = 20.00m;
    private const decimal PricePerKg = 3.00m;

    private readonly FuelSurcharge fuelSurcharge;

    public ExpressDeliveryPricePolicy(FuelSurcharge fuelSurcharge)
    {
        ArgumentNullException.ThrowIfNull(fuelSurcharge);
        this.fuelSurcharge = fuelSurcharge;
    }

    public ShippingMethod Method() => ShippingMethod.Express;

    public decimal PriceFor(Parcel parcel)
    {
        decimal baseAmount = BasePrice + parcel.WeightKg * PricePerKg;
        return fuelSurcharge.AddTo(baseAmount);
    }
}
