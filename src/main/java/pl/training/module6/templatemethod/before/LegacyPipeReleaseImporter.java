package pl.training.module6.templatemethod.before;

public final class LegacyPipeReleaseImporter {
    public ReleaseDraft importRelease(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("input must not be blank");
        }

        String[] fields = raw.split("\\|", -1);
        if (fields.length != 2) {
            throw new IllegalArgumentException("expected releaseId and service");
        }

        String releaseId = fields[0].trim();
        String service = fields[1].trim();
        if (releaseId.isBlank() || service.isBlank()) {
            throw new IllegalArgumentException("releaseId and service are required");
        }
        return new ReleaseDraft(releaseId, service);
    }
}
