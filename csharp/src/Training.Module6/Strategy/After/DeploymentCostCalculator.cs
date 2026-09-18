namespace Training.Module6.Strategy.After;

public sealed class DeploymentCostCalculator
{
    private readonly IDeploymentCostPolicy _policy;

    public DeploymentCostCalculator(IDeploymentCostPolicy policy)
    {
        ArgumentNullException.ThrowIfNull(policy);
        _policy = policy;
    }

    public long Calculate(long baseCostInCents)
    {
        if (baseCostInCents < 0)
        {
            throw new ArgumentException("base cost must not be negative");
        }
        return _policy.Calculate(baseCostInCents);
    }
}
