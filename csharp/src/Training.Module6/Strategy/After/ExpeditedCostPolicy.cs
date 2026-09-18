namespace Training.Module6.Strategy.After;

public sealed class ExpeditedCostPolicy : IDeploymentCostPolicy
{
    public long Calculate(long baseCostInCents) =>
        checked(baseCostInCents + baseCostInCents / 4);
}
