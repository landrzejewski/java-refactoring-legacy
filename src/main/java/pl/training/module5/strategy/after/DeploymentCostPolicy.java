package pl.training.module5.strategy.after;

@FunctionalInterface
public interface DeploymentCostPolicy {
    long calculate(long baseCostInCents);
}
