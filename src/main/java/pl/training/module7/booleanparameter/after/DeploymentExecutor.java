package pl.training.module7.booleanparameter.after;

public final class DeploymentExecutor {
    public String preview(String deploymentId) {
        return execute(deploymentId, ExecutionMode.PREVIEW);
    }

    public String deploy(String deploymentId) {
        return execute(deploymentId, ExecutionMode.DEPLOY);
    }

    private String execute(String deploymentId, ExecutionMode mode) {
        validate(deploymentId);
        return mode.resultPrefix() + deploymentId;
    }

    private static void validate(String deploymentId) {
        if (deploymentId == null || deploymentId.isBlank()) {
            throw new IllegalArgumentException("deploymentId must not be blank");
        }
    }

    private enum ExecutionMode {
        PREVIEW("preview:"),
        DEPLOY("deployed:");

        private final String resultPrefix;

        ExecutionMode(String resultPrefix) {
            this.resultPrefix = resultPrefix;
        }

        String resultPrefix() {
            return resultPrefix;
        }
    }
}
