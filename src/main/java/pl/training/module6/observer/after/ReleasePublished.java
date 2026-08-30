package pl.training.module6.observer.after;

public record ReleasePublished(String releaseId) {
    public ReleasePublished {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
    }
}
