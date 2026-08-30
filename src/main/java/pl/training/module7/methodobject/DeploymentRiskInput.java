package pl.training.module7.methodobject;

public record DeploymentRiskInput(
        int changedFiles,
        int criticalServices,
        int failedChecks,
        boolean rollbackTested) {

    public DeploymentRiskInput {
        requireNonNegative(changedFiles, "changedFiles");
        requireNonNegative(criticalServices, "criticalServices");
        requireNonNegative(failedChecks, "failedChecks");
    }

    private static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
    }
}
