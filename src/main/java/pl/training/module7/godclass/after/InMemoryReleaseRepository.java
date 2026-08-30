package pl.training.module7.godclass.after;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import pl.training.module7.godclass.PublishedRelease;

public final class InMemoryReleaseRepository implements ReleaseRepository {
    private final Map<String, PublishedRelease> releases =
            new LinkedHashMap<>();
    private final Consumer<String> eventSink;

    public InMemoryReleaseRepository() {
        this(ignored -> { });
    }

    public InMemoryReleaseRepository(Consumer<String> eventSink) {
        this.eventSink = Objects.requireNonNull(eventSink, "eventSink");
    }

    @Override
    public boolean existsById(String releaseId) {
        return releases.containsKey(releaseId);
    }

    @Override
    public void save(PublishedRelease release) {
        Objects.requireNonNull(release, "release");
        if (releases.containsKey(release.releaseId())) {
            throw new IllegalStateException(
                    "release already published: " + release.releaseId());
        }
        releases.put(release.releaseId(), release);
        eventSink.accept("save:" + release.releaseId());
    }

    @Override
    public List<PublishedRelease> findAll() {
        return List.copyOf(releases.values());
    }
}
