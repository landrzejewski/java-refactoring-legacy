package pl.training.module6.strategy.before;

import java.util.Objects;

public final class LegacyDeploymentCostCalculator {
    public long calculate(long baseCostInCents, DeploymentMode mode) {
        if (baseCostInCents < 0) {
            throw new IllegalArgumentException("base cost must not be negative");
        }

        return switch (Objects.requireNonNull(mode, "mode")) {
            case STANDARD -> baseCostInCents;
            case EXPEDITED -> Math.addExact(baseCostInCents, baseCostInCents / 4);
        };
    }

    public enum DeploymentMode {
        STANDARD,
        EXPEDITED
    }
}
