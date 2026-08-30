package pl.training.module6.composite.before;

import java.util.List;
import java.util.Objects;

public final class LegacyPathPlan {
    private final List<Entry> entries;

    public LegacyPathPlan(List<Entry> entries) {
        this.entries = List.copyOf(Objects.requireNonNull(entries, "entries"));
    }

    public long totalMinutes() {
        long total = 0;
        for (Entry entry : entries) {
            total = Math.addExact(total, entry.minutes());
        }
        return total;
    }

    public List<String> taskNamesBelow(String path) {
        String prefix = requirePath(path);
        return entries.stream()
                .filter(entry -> entry.path().startsWith(prefix + "/"))
                .map(Entry::taskName)
                .toList();
    }

    public record Entry(String path, long minutes) {
        public Entry {
            path = requirePath(path);
            if (!path.contains("/")) {
                throw new IllegalArgumentException("task path must contain a parent");
            }
            if (minutes < 0) {
                throw new IllegalArgumentException("minutes must not be negative");
            }
        }

        private String taskName() {
            return path.substring(path.lastIndexOf('/') + 1);
        }
    }

    private static String requirePath(String path) {
        Objects.requireNonNull(path, "path");
        if (path.isBlank() || path.startsWith("/") || path.endsWith("/")
                || path.contains("//")) {
            throw new IllegalArgumentException("invalid path: " + path);
        }
        for (String segment : path.split("/", -1)) {
            if (segment.isBlank()) {
                throw new IllegalArgumentException("invalid path: " + path);
            }
        }
        return path;
    }
}
