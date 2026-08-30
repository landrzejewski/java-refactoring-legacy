package pl.training.module6.observer.before;

import java.util.Objects;
import java.util.function.Consumer;

public final class LegacyReleasePublisher {
    private final Consumer<PublishedRelease> auditLog;
    private final Consumer<PublishedRelease> metrics;

    public LegacyReleasePublisher(
            Consumer<PublishedRelease> auditLog,
            Consumer<PublishedRelease> metrics) {
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog");
        this.metrics = Objects.requireNonNull(metrics, "metrics");
    }

    public void publish(PublishedRelease release) {
        auditLog.accept(Objects.requireNonNull(release, "release"));
        metrics.accept(release);
    }

    public record PublishedRelease(String releaseId) {
        public PublishedRelease {
            if (releaseId == null || releaseId.isBlank()) {
                throw new IllegalArgumentException("releaseId must not be blank");
            }
        }
    }
}
