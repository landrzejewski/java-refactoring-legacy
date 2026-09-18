namespace Training.Module6.Strategy.Before;

public sealed class LegacyDeploymentCostCalculator
{
    public long Calculate(long baseCostInCents, DeploymentMode mode)
    {
        if (baseCostInCents < 0)
        {
            throw new ArgumentException("base cost must not be negative");
        }

        return mode switch
        {
            DeploymentMode.Standard => baseCostInCents,
            DeploymentMode.Expedited => checked(baseCostInCents + baseCostInCents / 4),
            _ => throw new ArgumentOutOfRangeException(nameof(mode), mode, null)
        };
    }

    public enum DeploymentMode
    {
        Standard,
        Expedited
    }
}
