package pl.training.module7.duplication.before;

import java.util.Locale;
import java.util.Objects;

public final class LegacyArtifactPublisher {
    public String publishSnapshot(String artifactName, int buildNumber) {
        Objects.requireNonNull(artifactName, "artifactName");
        if (artifactName.isBlank()) {
            throw new IllegalArgumentException("artifactName must not be blank");
        }
        if (buildNumber <= 0) {
            throw new IllegalArgumentException(
                    "buildNumber must be greater than zero");
        }

        String normalizedName = artifactName.strip().toLowerCase(Locale.ROOT);
        return normalizedName + ":" + buildNumber + "-SNAPSHOT";
    }

    public String publishRelease(String artifactName, int buildNumber) {
        Objects.requireNonNull(artifactName, "artifactName");
        if (artifactName.isBlank()) {
            throw new IllegalArgumentException("artifactName must not be blank");
        }
        if (buildNumber <= 0) {
            throw new IllegalArgumentException(
                    "buildNumber must be greater than zero");
        }

        String normalizedName = artifactName.strip().toLowerCase(Locale.ROOT);
        return normalizedName + ":" + buildNumber;
    }
}
