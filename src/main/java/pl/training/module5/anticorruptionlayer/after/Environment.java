package pl.training.module5.anticorruptionlayer.after;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model domenowy środowiska - nazwany w języku planowania wdrożeń, nie CMDB.
 */
public record Environment(String name, Tier tier, boolean active, LocalDate lastDeployment) {
    public Environment {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        Objects.requireNonNull(tier, "tier");
        Objects.requireNonNull(lastDeployment, "lastDeployment");
    }

    public enum Tier {
        PRODUCTION,
        TEST,
        DEVELOPMENT
    }
}
