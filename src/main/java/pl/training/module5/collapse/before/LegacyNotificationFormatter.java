package pl.training.module5.collapse.before;

import java.util.Objects;

public class LegacyNotificationFormatter {
    public String format(String recipient, String message) {
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(message, "message must not be null");

        return "To: " + recipient + "\nMessage: " + message;
    }
}
