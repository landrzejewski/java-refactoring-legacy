package pl.training.module7.breakdependencies.after;

import java.util.Objects;

import pl.training.module7.breakdependencies.DeploymentDecision;

public final class DeploymentWindowService {
    private final MaintenanceWindows maintenanceWindows;

    public DeploymentWindowService(MaintenanceWindows maintenanceWindows) {
        this.maintenanceWindows = Objects.requireNonNull(
                maintenanceWindows, "maintenanceWindows");
    }

    public DeploymentDecision schedule(String service, int hourUtc) {
        validate(service, hourUtc);
        return maintenanceWindows.allows(service, hourUtc)
                ? DeploymentDecision.ALLOWED
                : DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW;
    }

    private static void validate(String service, int hourUtc) {
        Objects.requireNonNull(service, "service");
        if (service.isBlank()) {
            throw new IllegalArgumentException("service must not be blank");
        }
        if (hourUtc < 0 || hourUtc > 23) {
            throw new IllegalArgumentException(
                    "hourUtc must be between 0 and 23");
        }
    }
}
