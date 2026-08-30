package pl.training.module7.middleman.before;

import java.util.Objects;

import pl.training.module7.middleman.DeploymentStatus;

public final class ReleaseService {
    private final DeploymentRegistry registry;

    public ReleaseService(DeploymentRegistry registry) {
        this.registry = Objects.requireNonNull(
                registry, "registry must not be null");
    }

    public void update(String deploymentId, DeploymentStatus status) {
        registry.update(deploymentId, status);
    }

    public DeploymentStatus statusOf(String deploymentId) {
        return registry.statusOf(deploymentId);
    }
}
