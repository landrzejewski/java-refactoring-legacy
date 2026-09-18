using System.Collections.Frozen;

namespace Training.Module3.Domain;

public sealed class DeliveryPriceCalculator
{
    private readonly FrozenDictionary<ShippingMethod, IDeliveryPricePolicy> policies;

    public DeliveryPriceCalculator(
        IReadOnlyCollection<IDeliveryPricePolicy> policies)
    {
        ArgumentNullException.ThrowIfNull(policies);

        if (policies.Count == 0)
        {
            throw new ArgumentException("At least one policy is required", nameof(policies));
        }

        Dictionary<ShippingMethod, IDeliveryPricePolicy> indexedPolicies = [];
        foreach (IDeliveryPricePolicy policy in policies)
        {
            ArgumentNullException.ThrowIfNull(policy);
            ShippingMethod method = policy.Method();

            if (!indexedPolicies.TryAdd(method, policy))
            {
                throw new ArgumentException(
                    "Duplicate policy for method: " + method.ToString().ToUpperInvariant(),
                    nameof(policies));
            }
        }
        this.policies = indexedPolicies.ToFrozenDictionary();
    }

    public decimal PriceFor(ShippingMethod method, Parcel parcel)
    {
        ArgumentNullException.ThrowIfNull(parcel);

        if (!policies.TryGetValue(method, out IDeliveryPricePolicy? policy))
        {
            throw new ArgumentException(
                "No pricing policy for method: " + method.ToString().ToUpperInvariant(),
                nameof(method));
        }
        return policy.PriceFor(parcel);
    }
}
