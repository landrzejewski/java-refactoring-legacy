package pl.training.module6.strategy.after;

@FunctionalInterface
public interface DeploymentCostPolicy {
    long calculate(long baseCostInCents);
}
