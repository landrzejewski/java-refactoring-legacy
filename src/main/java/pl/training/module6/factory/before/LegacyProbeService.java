package pl.training.module6.factory.before;

import java.util.Objects;

public final class LegacyProbeService {
    public String check(ProbeKind kind, String target) {
        DeploymentProbe probe = switch (Objects.requireNonNull(kind, "kind")) {
            case HTTP -> new HttpProbe(target);
            case QUEUE -> new QueueProbe(target);
        };
        return probe.check();
    }

    public enum ProbeKind {
        HTTP,
        QUEUE
    }
}
