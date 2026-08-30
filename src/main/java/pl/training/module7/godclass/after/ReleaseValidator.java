package pl.training.module7.godclass.after;

import pl.training.module7.godclass.PublishedRelease;

public final class ReleaseValidator {
    public PublishedRelease validate(
            String releaseId,
            String service,
            String version) {
        requireText(releaseId, "releaseId");
        requireText(service, "service");
        requireText(version, "version");
        return new PublishedRelease(releaseId, service, version);
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
