package pl.training.module5.stage0;

import java.util.Locale;
import java.util.Objects;

public final class SmsNotification {
    private final String messageId;
    private final String senderId;
    private final String body;
    private final boolean deliveryReceipt;

    public SmsNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        this.messageId = normalized(messageId, "messageId");
        this.senderId = normalized(senderId, "senderId")
                .toUpperCase(Locale.ROOT);
        this.body = normalized(body, "body");
        this.deliveryReceipt = deliveryReceipt;
    }

    public String messageId() {
        return messageId;
    }

    public String summary() {
        return messageId + "|" + senderId + "|" + body + "|SMS";
    }

    public String dispatch(boolean successful) {
        String result = summary() + (successful ? "|SENT" : "|FAILED");
        return successful ? appendReceipt(result) : result;
    }

    private String appendReceipt(String result) {
        return deliveryReceipt ? result + "|RECEIPT" : result;
    }

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
