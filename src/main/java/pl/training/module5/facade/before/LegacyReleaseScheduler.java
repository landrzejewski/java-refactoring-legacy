package pl.training.module5.facade.before;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Drugi klient z tą samą orkiestracją, skopiowaną i nieco inaczej zapisaną.
 * Zmiana kolejności kroków w jednym miejscu łatwo rozjedzie się z drugim.
 */
public final class LegacyReleaseScheduler {
    private final ArtifactStore store;
    private final ConfigRenderer renderer;
    private final DeploymentQueue queue;
    private final ReleaseNotifier notifier;

    public LegacyReleaseScheduler(
            ArtifactStore store,
            ConfigRenderer renderer,
            DeploymentQueue queue,
            ReleaseNotifier notifier) {
        this.store = Objects.requireNonNull(store, "store");
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.queue = Objects.requireNonNull(queue, "queue");
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public List<String> publishNightly(List<String> releaseIds) {
        List<String> published = new ArrayList<>();
        for (String releaseId : releaseIds) {
            if (releaseId == null || releaseId.isBlank()) {
                throw new IllegalArgumentException("releaseId must not be blank");
            }
            String artifact = store.upload(releaseId);
            String config = renderer.render(releaseId, "staging");
            queue.enqueue(artifact, config);
            notifier.announce(releaseId, "staging");
            published.add("published " + releaseId + " to staging");
        }
        return published;
    }
}
