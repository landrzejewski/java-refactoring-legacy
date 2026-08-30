package pl.training.module7.middleman.after;

import java.util.Objects;

public final class ReleaseDashboard {
    private final DeploymentRegistry registry;

    public ReleaseDashboard(DeploymentRegistry registry) {
        this.registry = Objects.requireNonNull(
                registry, "registry must not be null");
    }

    public String render(String deploymentId) {
        return deploymentId + " -> " + registry.statusOf(deploymentId);
    }
}
