package pl.training.module6.templatemethod.before;

public final class LegacyKeyValueReleaseImporter {
    public ReleaseDraft importRelease(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("input must not be blank");
        }

        String releaseId = null;
        String service = null;
        for (String field : raw.split(";", -1)) {
            String[] pair = field.split("=", 2);
            if (pair.length != 2) {
                throw new IllegalArgumentException("expected key=value");
            }
            switch (pair[0].trim()) {
                case "id" -> releaseId = pair[1].trim();
                case "service" -> service = pair[1].trim();
                default -> throw new IllegalArgumentException("unknown field: " + pair[0]);
            }
        }

        if (releaseId == null || releaseId.isBlank()
                || service == null || service.isBlank()) {
            throw new IllegalArgumentException("releaseId and service are required");
        }
        return new ReleaseDraft(releaseId, service);
    }
}
