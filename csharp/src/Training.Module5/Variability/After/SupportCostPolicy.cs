namespace Training.Module5.Variability.After;

/// <summary>
/// Strategy: algorytm kosztu wsparcia. Wybierany raz przez klienta, potem tylko wywoływany.
/// Java: <c>@FunctionalInterface</c> ze stałymi; tu delegat, a stałe w <see cref="SupportCostPolicies"/>.
/// </summary>
public delegate long SupportCostPolicy(int hours);

public static class SupportCostPolicies
{
    public static readonly SupportCostPolicy Standard = hours => hours * 100L;
    public static readonly SupportCostPolicy Premium = hours => 500 + hours * 80L;
    public static readonly SupportCostPolicy Enterprise = hours => Math.Max(2_000, hours * 60L);

    public static SupportCostPolicy ForTier(CustomerTier tier) => tier switch
    {
        CustomerTier.Standard => Standard,
        CustomerTier.Premium => Premium,
        CustomerTier.Enterprise => Enterprise,
        _ => throw new ArgumentOutOfRangeException(nameof(tier), tier, "unknown tier")
    };
}
