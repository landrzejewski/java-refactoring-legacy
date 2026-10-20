package pl.training.module5.facade.after;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Fasada jako strażnik spójności: jedna operacja wymusza kolejność kroków i walidację.
 * Podsystemy są pakietowo-prywatne, więc klient spoza pakietu nie może ich złożyć
 * w złej kolejności ani pominąć kroku.
 */
public final class ReleasePublishingFacade {
    private static final Set<String> ENVIRONMENTS = Set.of("staging", "prod");

    private final ArtifactStore store;
    private final ConfigRenderer renderer;
    private final DeploymentQueue queue;
    private final ReleaseNotifier notifier;

    public ReleasePublishingFacade(Consumer<String> effects) {
        this.store = new ArtifactStore(effects);
        this.renderer = new ConfigRenderer(effects);
        this.queue = new DeploymentQueue(effects);
        this.notifier = new ReleaseNotifier(effects);
    }

    public String publish(String releaseId, String environment) {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        Objects.requireNonNull(environment, "environment");
        String target = environment.trim().toLowerCase(Locale.ROOT);
        if (!ENVIRONMENTS.contains(target)) {
            throw new IllegalArgumentException("unknown environment: " + environment);
        }
        String artifact = store.upload(releaseId);
        String config = renderer.render(releaseId, target);
        queue.enqueue(artifact, config);
        notifier.announce(releaseId, target);
        return "published " + releaseId + " to " + target;
    }
}
