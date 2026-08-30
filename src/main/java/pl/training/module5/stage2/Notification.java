package pl.training.module5.stage2;

import java.util.Locale;
import java.util.Objects;

abstract class Notification {
    private final String messageId;
    private final String senderId;
    private final String body;

    protected Notification(
            String messageId,
            String senderId,
            String body) {
        this.messageId = normalized(messageId, "messageId");
        this.senderId = normalized(senderId, "senderId")
                .toUpperCase(Locale.ROOT);
        this.body = normalized(body, "body");
    }

    public final String messageId() {
        return messageId;
    }

    public final String summary() {
        return messageId + "|" + senderId + "|" + body + "|" + channel();
    }

    protected final String dispatchResult(boolean successful) {
        return summary() + (successful ? "|SENT" : "|FAILED");
    }

    protected abstract String channel();

    public abstract String dispatch(boolean successful);

    private static String normalized(String value, String fieldName) {
        String normalized = Objects.requireNonNull(
                value,
                fieldName + " must not be null").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }
        return normalized;
    }
}
