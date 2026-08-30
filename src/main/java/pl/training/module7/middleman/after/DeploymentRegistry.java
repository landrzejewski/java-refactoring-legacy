package pl.training.module7.middleman.after;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import pl.training.module7.middleman.DeploymentStatus;

public final class DeploymentRegistry {
    private final Map<String, DeploymentStatus> statuses = new HashMap<>();

    public void update(String deploymentId, DeploymentStatus status) {
        validateDeploymentId(deploymentId);
        statuses.put(
                deploymentId,
                Objects.requireNonNull(status, "status must not be null"));
    }

    public DeploymentStatus statusOf(String deploymentId) {
        validateDeploymentId(deploymentId);
        return statuses.getOrDefault(deploymentId, DeploymentStatus.UNKNOWN);
    }

    private static void validateDeploymentId(String deploymentId) {
        if (deploymentId == null || deploymentId.isBlank()) {
            throw new IllegalArgumentException("deploymentId must not be blank");
        }
    }
}
