package pl.training.module7.godclass.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import pl.training.module7.godclass.PublishedRelease;

public final class ReleaseNotifier {
    private final List<String> notifications = new ArrayList<>();
    private final Consumer<String> eventSink;

    public ReleaseNotifier() {
        this(ignored -> { });
    }

    public ReleaseNotifier(Consumer<String> eventSink) {
        this.eventSink = Objects.requireNonNull(eventSink, "eventSink");
    }

    public void notifyPublished(PublishedRelease release) {
        Objects.requireNonNull(release, "release");
        notifications.add("release-published:" + release.releaseId());
        eventSink.accept("notify:" + release.releaseId());
    }

    public List<String> notifications() {
        return List.copyOf(notifications);
    }
}
