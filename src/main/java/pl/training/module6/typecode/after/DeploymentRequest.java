package pl.training.module6.typecode.after;

import java.util.Objects;

public record DeploymentRequest(String releaseId, DeploymentZone zone) {
    public DeploymentRequest {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        Objects.requireNonNull(zone, "zone");
    }

    public boolean requiresApproval() {
        return zone.requiresApproval();
    }
}
