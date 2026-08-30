package pl.training.module6.adapter.after;

import java.util.Objects;

public final class NotificationService {
    private final ReleaseNotifier notifier;

    public NotificationService(ReleaseNotifier notifier) {
        this.notifier = Objects.requireNonNull(notifier, "notifier");
    }

    public String notify(ReleaseMessage message) {
        return notifier.send(Objects.requireNonNull(message, "message"));
    }
}
