namespace Training.Module5.Strategy.After;

public interface IDeploymentCostPolicy
{
    long Calculate(long baseCostInCents);
}
