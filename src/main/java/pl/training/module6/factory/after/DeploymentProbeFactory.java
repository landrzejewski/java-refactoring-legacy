package pl.training.module6.factory.after;

import java.util.Objects;

public final class DeploymentProbeFactory {
    public DeploymentProbe create(ProbeKind kind, String target) {
        Objects.requireNonNull(kind, "kind");
        if (target == null || target.isBlank()) {
            String field = kind == ProbeKind.HTTP ? "endpoint" : "queueName";
            throw new IllegalArgumentException(field + " must not be blank");
        }

        return switch (kind) {
            case HTTP -> new HttpProbe(target);
            case QUEUE -> new QueueProbe(target);
        };
    }

    public enum ProbeKind {
        HTTP,
        QUEUE
    }

    private record HttpProbe(String endpoint) implements DeploymentProbe {
        @Override
        public String check() {
            return "http-ok:" + endpoint;
        }
    }

    private record QueueProbe(String queueName) implements DeploymentProbe {
        @Override
        public String check() {
            return "queue-ok:" + queueName;
        }
    }
}
