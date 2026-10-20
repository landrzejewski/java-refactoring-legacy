namespace Training.Module2.Domain;

public interface IDeliveryPricePolicy
{
    /// <summary>
    /// Returns the stable shipping method handled by this policy.
    /// </summary>
    ShippingMethod Method();

    /// <summary>
    /// Returns a deterministic, non-negative amount with scale two for every
    /// valid parcel, without changing the parcel or producing side effects.
    /// </summary>
    decimal PriceFor(Parcel parcel);
}
