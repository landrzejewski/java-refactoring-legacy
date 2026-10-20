package pl.training.module5.strategy.after;

public final class ExpeditedCostPolicy implements DeploymentCostPolicy {
    @Override
    public long calculate(long baseCostInCents) {
        return Math.addExact(baseCostInCents, baseCostInCents / 4);
    }
}
