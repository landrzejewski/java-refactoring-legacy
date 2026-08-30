package pl.training.module7.breakmethod.after;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import pl.training.module7.breakmethod.ManifestEntry;

public final class ReleaseManifestBuilder {
    public String build(List<ManifestEntry> entries) {
        List<ManifestEntry> validatedEntries = validateAndCopy(entries);
        List<ManifestEntry> orderedEntries = order(validatedEntries);
        return render(orderedEntries);
    }

    private List<ManifestEntry> validateAndCopy(List<ManifestEntry> entries) {
        Objects.requireNonNull(entries, "entries");

        var copy = new ArrayList<ManifestEntry>(entries.size());
        for (ManifestEntry entry : entries) {
            Objects.requireNonNull(entry, "entries must not contain null");
            Objects.requireNonNull(entry.artifact(), "artifact");
            if (entry.artifact().isBlank()) {
                throw new IllegalArgumentException("artifact must not be blank");
            }
            Objects.requireNonNull(entry.checksum(), "checksum");
            if (entry.checksum().isBlank()) {
                throw new IllegalArgumentException("checksum must not be blank");
            }
            if (entry.deploymentOrder() < 0) {
                throw new IllegalArgumentException(
                        "deploymentOrder must not be negative");
            }
            copy.add(entry);
        }
        return copy;
    }

    private List<ManifestEntry> order(List<ManifestEntry> entries) {
        return entries.stream()
                .sorted(
                        Comparator.comparingInt(ManifestEntry::deploymentOrder)
                                .thenComparing(ManifestEntry::artifact))
                .toList();
    }

    private String render(List<ManifestEntry> entries) {
        var manifest = new StringJoiner("\n");
        for (ManifestEntry entry : entries) {
            manifest.add(
                    entry.deploymentOrder()
                            + "|"
                            + entry.artifact()
                            + "|"
                            + entry.checksum());
        }
        return manifest.toString();
    }
}
