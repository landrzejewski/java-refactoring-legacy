package pl.training.module6.templatemethod.after;

public abstract class ReleaseImporter {
    public final ReleaseDraft importRelease(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("input must not be blank");
        }

        Fields fields = parse(raw);
        String releaseId = fields.releaseId();
        String service = fields.service();
        if (releaseId == null || releaseId.isBlank()
                || service == null || service.isBlank()) {
            throw new IllegalArgumentException("releaseId and service are required");
        }
        return new ReleaseDraft(releaseId, service);
    }

    protected abstract Fields parse(String raw);

    protected final Fields fields(String releaseId, String service) {
        return new Fields(releaseId, service);
    }

    protected record Fields(String releaseId, String service) {
    }
}
