package pl.training.module6.strategy.after;

public final class ExpeditedCostPolicy implements DeploymentCostPolicy {
    @Override
    public long calculate(long baseCostInCents) {
        return Math.addExact(baseCostInCents, baseCostInCents / 4);
    }
}
