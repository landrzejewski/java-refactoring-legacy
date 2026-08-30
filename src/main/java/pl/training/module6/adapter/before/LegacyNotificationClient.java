package pl.training.module6.adapter.before;

import java.util.Objects;

public final class LegacyNotificationClient {
    public String notifyUsingPreferred(
            ReleaseNotifier notifier,
            String recipient,
            String releaseId) {
        validate(recipient, releaseId);
        return Objects.requireNonNull(notifier, "notifier")
                .send(recipient, releaseId);
    }

    public String notifyUsingLegacy(
            LegacyMessageGateway gateway,
            String recipient,
            String releaseId) {
        validate(recipient, releaseId);
        return Objects.requireNonNull(gateway, "gateway")
                .transmit(recipient, "release:" + releaseId);
    }

    private static void validate(String recipient, String releaseId) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("recipient must not be blank");
        }
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
    }

    @FunctionalInterface
    public interface ReleaseNotifier {
        String send(String recipient, String releaseId);
    }

    @FunctionalInterface
    public interface LegacyMessageGateway {
        String transmit(String destination, String body);
    }
}
