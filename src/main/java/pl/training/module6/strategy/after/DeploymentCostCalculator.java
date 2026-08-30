package pl.training.module6.strategy.after;

import java.util.Objects;

public final class DeploymentCostCalculator {
    private final DeploymentCostPolicy policy;

    public DeploymentCostCalculator(DeploymentCostPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    public long calculate(long baseCostInCents) {
        if (baseCostInCents < 0) {
            throw new IllegalArgumentException("base cost must not be negative");
        }
        return policy.calculate(baseCostInCents);
    }
}
