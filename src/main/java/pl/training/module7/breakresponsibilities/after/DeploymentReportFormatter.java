package pl.training.module7.breakresponsibilities.after;

import java.util.Objects;

import pl.training.module7.breakresponsibilities.DeploymentMetrics;

public final class DeploymentReportFormatter {
    public String format(DeploymentMetrics metrics) {
        Objects.requireNonNull(metrics, "metrics");
        return "deployments=" + metrics.deployments()
                + ";failures=" + metrics.failures()
                + ";avgLeadTimeMinutes="
                + metrics.averageLeadTimeMinutes();
    }
}
