package pl.training.module7.booleanparameter.before;

public final class LegacyDeploymentExecutor {
    public String execute(String deploymentId, boolean dryRun) {
        validate(deploymentId);

        if (dryRun) {
            return "preview:" + deploymentId;
        }
        return "deployed:" + deploymentId;
    }

    private static void validate(String deploymentId) {
        if (deploymentId == null || deploymentId.isBlank()) {
            throw new IllegalArgumentException("deploymentId must not be blank");
        }
    }
}
