package pl.training.module7.godclass.before;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.training.module7.godclass.PublishedRelease;

public final class LegacyReleaseManager {
    private final Map<String, PublishedRelease> releases =
            new LinkedHashMap<>();
    private final List<String> auditEntries = new ArrayList<>();
    private final List<String> notifications = new ArrayList<>();
    private final List<String> events = new ArrayList<>();

    public PublishedRelease publish(
            String releaseId,
            String service,
            String version) {
        requireText(releaseId, "releaseId");
        requireText(service, "service");
        requireText(version, "version");
        if (releases.containsKey(releaseId)) {
            throw new IllegalStateException(
                    "release already published: " + releaseId);
        }

        var release = new PublishedRelease(releaseId, service, version);
        releases.put(releaseId, release);
        events.add("save:" + releaseId);
        auditEntries.add("published:" + releaseId);
        events.add("audit:" + releaseId);
        notifications.add("release-published:" + releaseId);
        events.add("notify:" + releaseId);
        return release;
    }

    public List<PublishedRelease> releases() {
        return List.copyOf(releases.values());
    }

    public List<String> auditEntries() {
        return List.copyOf(auditEntries);
    }

    public List<String> notifications() {
        return List.copyOf(notifications);
    }

    public List<String> events() {
        return List.copyOf(events);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
