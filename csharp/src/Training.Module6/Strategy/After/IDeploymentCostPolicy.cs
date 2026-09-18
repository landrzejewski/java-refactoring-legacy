namespace Training.Module6.Strategy.After;

public interface IDeploymentCostPolicy
{
    long Calculate(long baseCostInCents);
}
