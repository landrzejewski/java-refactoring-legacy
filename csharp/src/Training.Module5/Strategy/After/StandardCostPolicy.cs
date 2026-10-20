namespace Training.Module5.Strategy.After;

public sealed class StandardCostPolicy : IDeploymentCostPolicy
{
    public long Calculate(long baseCostInCents) => baseCostInCents;
}
