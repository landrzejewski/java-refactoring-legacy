namespace Training.Module6.Strategy.After;

public sealed class StandardCostPolicy : IDeploymentCostPolicy
{
    public long Calculate(long baseCostInCents) => baseCostInCents;
}
