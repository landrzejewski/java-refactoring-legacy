package pl.training.module5.facade.before;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Klient sam orkiestruje cztery podsystemy i sam pilnuje kolejności oraz walidacji.
 * Ta sama wiedza jest skopiowana w {@link LegacyReleaseScheduler}.
 */
public final class LegacyReleaseCli {
    private final ArtifactStore store;
    private final ConfigRenderer renderer;
    private final DeploymentQueue queue;
    private final ReleaseNotifier notifier;

    public LegacyReleaseCli(
            ArtifactStore store,
            ConfigRenderer renderer,
            DeploymentQueue queue,
            ReleaseNotifier notifier) {
        this.store = Objects.requireNonNull(store, "store");
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.queue = Objects.requireNonNull(queue, "queue");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public String publish(String releaseId, String environment) {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        String target = environment.trim().toLowerCase(Locale.ROOT);
        if (!Set.of("staging", "prod").contains(target)) {
            throw new IllegalArgumentException("unknown environment: " + environment);
        }
        String artifact = store.upload(releaseId);
        String config = renderer.render(releaseId, target);
        queue.enqueue(artifact, config);
        notifier.announce(releaseId, target);
        return "published " + releaseId + " to " + target;
    }
}
