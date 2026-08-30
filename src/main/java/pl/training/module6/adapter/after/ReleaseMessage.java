package pl.training.module6.adapter.after;

public record ReleaseMessage(String recipient, String releaseId) {
    public ReleaseMessage {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("recipient must not be blank");
        }
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
    }
}
