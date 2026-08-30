package pl.training.module7.breakresponsibilities.after;

import java.util.List;
import java.util.Objects;

import pl.training.module7.breakresponsibilities.DeploymentMetrics;
import pl.training.module7.breakresponsibilities.DeploymentSample;

public final class DeploymentMetricsCalculator {
    public DeploymentMetrics calculate(List<DeploymentSample> samples) {
        Objects.requireNonNull(samples, "samples");

        int deployments = 0;
        int failures = 0;
        long totalLeadTimeMinutes = 0;

        for (DeploymentSample sample : samples) {
            Objects.requireNonNull(sample, "sample");
            deployments++;
            if (!sample.successful()) {
                failures++;
            }
            totalLeadTimeMinutes = Math.addExact(
                    totalLeadTimeMinutes, sample.leadTimeMinutes());
        }

        long averageLeadTimeMinutes = deployments == 0
                ? 0
                : totalLeadTimeMinutes / deployments;

        return new DeploymentMetrics(
                deployments, failures, averageLeadTimeMinutes);
    }
}
