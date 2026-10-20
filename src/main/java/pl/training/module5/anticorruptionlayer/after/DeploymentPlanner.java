package pl.training.module5.anticorruptionlayer.after;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Reguły planowania wyrażone wyłącznie modelem domenowym.
 */
public final class DeploymentPlanner {
    private final EnvironmentDirectory environments;

    public DeploymentPlanner(EnvironmentDirectory environments) {
        this.environments = Objects.requireNonNull(environments, "environments");
    }

    public List<String> canaryTargets() {
        return environments.all().stream()
                .filter(Environment::active)
                .filter(environment -> environment.tier() != Environment.Tier.PRODUCTION)
                .map(Environment::name)
                .sorted()
                .toList();
    }

    public List<String> staleEnvironments(LocalDate today) {
        LocalDate threshold = today.minusDays(30);
        return environments.all().stream()
                .filter(Environment::active)
                .filter(environment -> environment.lastDeployment().isBefore(threshold))
                .map(Environment::name)
                .sorted()
                .toList();
    }

    public boolean isProduction(String name) {
        return environments.all().stream()
                .anyMatch(environment -> environment.name().equals(name)
                        && environment.tier() == Environment.Tier.PRODUCTION);
    }
}
