package pl.training.module6.factory.after;

import java.util.Objects;

import pl.training.module6.factory.after.DeploymentProbeFactory.ProbeKind;

public final class ProbeService {
    private final DeploymentProbeFactory factory;

    public ProbeService(DeploymentProbeFactory factory) {
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    public String check(ProbeKind kind, String target) {
        return factory.create(kind, target).check();
    }
}
