package pl.training.module7.breakresponsibilities;

public record DeploymentMetrics(
        int deployments,
        int failures,
        long averageLeadTimeMinutes) {

    public DeploymentMetrics {
        if (deployments < 0) {
            throw new IllegalArgumentException(
                    "deployments must not be negative");
        }
        if (failures < 0 || failures > deployments) {
            throw new IllegalArgumentException(
                    "failures must be between 0 and deployments");
        }
        if (averageLeadTimeMinutes < 0) {
            throw new IllegalArgumentException(
                    "averageLeadTimeMinutes must not be negative");
        }
        if (deployments == 0 && averageLeadTimeMinutes != 0) {
            throw new IllegalArgumentException(
                    "empty metrics must have zero average lead time");
        }
    }
}
