package pl.training.module7.godclass.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import pl.training.module7.godclass.PublishedRelease;

public final class AuditTrail {
    private final List<String> entries = new ArrayList<>();
    private final Consumer<String> eventSink;

    public AuditTrail() {
        this(ignored -> { });
    }

    public AuditTrail(Consumer<String> eventSink) {
        this.eventSink = Objects.requireNonNull(eventSink, "eventSink");
    }

    public void recordPublished(PublishedRelease release) {
        Objects.requireNonNull(release, "release");
        entries.add("published:" + release.releaseId());
        eventSink.accept("audit:" + release.releaseId());
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
