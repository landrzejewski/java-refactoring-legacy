package pl.training.module7.middleman.before;

import java.util.Objects;

public final class ReleaseDashboard {
    private final ReleaseService releaseService;

    public ReleaseDashboard(ReleaseService releaseService) {
        this.releaseService = Objects.requireNonNull(
                releaseService, "releaseService must not be null");
    }

    public String render(String deploymentId) {
        return deploymentId + " -> " + releaseService.statusOf(deploymentId);
    }
}
