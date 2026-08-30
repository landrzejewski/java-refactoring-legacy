package pl.training.module8.boyscout;

import java.util.Objects;

public record DeploymentResult(
        DeploymentStatus status,
        String environment,
        String description) {
    public DeploymentResult {
        Objects.requireNonNull(status, "status");
        environment = normalized(environment, "environment");
        description = normalized(description, "description");
    }

    private static String normalized(String value, String name) {
        Objects.requireNonNull(value, name);
        String normalized = value.strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }
}
