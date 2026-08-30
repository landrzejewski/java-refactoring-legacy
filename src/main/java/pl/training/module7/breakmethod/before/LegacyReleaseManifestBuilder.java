package pl.training.module7.breakmethod.before;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import pl.training.module7.breakmethod.ManifestEntry;

public final class LegacyReleaseManifestBuilder {
    public String build(List<ManifestEntry> entries) {
        Objects.requireNonNull(entries, "entries");

        var validatedEntries = new ArrayList<ManifestEntry>(entries.size());
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
            validatedEntries.add(entry);
        }

        validatedEntries.sort(
                Comparator.comparingInt(ManifestEntry::deploymentOrder)
                        .thenComparing(ManifestEntry::artifact));

        var manifest = new StringJoiner("\n");
        for (ManifestEntry entry : validatedEntries) {
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
