package pl.training.module6.singleton.before;

import java.time.Duration;

public final class LegacyDeploymentDefaults {
    private final Duration healthCheckTimeout = Duration.ofSeconds(30);

    public Duration healthCheckTimeout() {
        return healthCheckTimeout;
    }
}
