package pl.training.module5.singleton;

import java.time.Duration;

public enum DeploymentDefaults {
    INSTANCE;

    private final Duration healthCheckTimeout = Duration.ofSeconds(30);

    public Duration healthCheckTimeout() {
        return healthCheckTimeout;
    }
}
