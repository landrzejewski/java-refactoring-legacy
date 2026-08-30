package pl.training.module6.strategy.after;

public final class StandardCostPolicy implements DeploymentCostPolicy {
    @Override
    public long calculate(long baseCostInCents) {
        return baseCostInCents;
    }
}
