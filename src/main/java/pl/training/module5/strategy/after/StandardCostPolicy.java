package pl.training.module5.strategy.after;

public final class StandardCostPolicy implements DeploymentCostPolicy {
    @Override
    public long calculate(long baseCostInCents) {
        return baseCostInCents;
    }
}
