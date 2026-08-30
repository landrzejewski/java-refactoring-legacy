package pl.training.module7.duplication.after;

import java.util.Locale;
import java.util.Objects;

public final class ArtifactPublisher {
    public String publishSnapshot(String artifactName, int buildNumber) {
        return publish(artifactName, buildNumber, "-SNAPSHOT");
    }

    public String publishRelease(String artifactName, int buildNumber) {
        return publish(artifactName, buildNumber, "");
    }

    private String publish(
            String artifactName,
            int buildNumber,
            String qualifier) {

        String normalizedName = validateAndNormalize(
                artifactName,
                buildNumber);
        return normalizedName + ":" + buildNumber + qualifier;
    }

    private String validateAndNormalize(
            String artifactName,
            int buildNumber) {

        Objects.requireNonNull(artifactName, "artifactName");
        if (artifactName.isBlank()) {
            throw new IllegalArgumentException("artifactName must not be blank");
        }
        if (buildNumber <= 0) {
            throw new IllegalArgumentException(
                    "buildNumber must be greater than zero");
        }

        return artifactName.strip().toLowerCase(Locale.ROOT);
    }
}
