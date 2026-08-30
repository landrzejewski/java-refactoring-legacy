package pl.training.module5.stage3;

import java.util.List;
import java.util.Objects;

public final class NotificationBatch {
    public List<String> dispatchAll(
            List<? extends OutboundNotification> notifications,
            boolean successful) {
        Objects.requireNonNull(notifications, "notifications must not be null");
        var validatedNotifications = notifications.stream()
                .map(notification -> Objects.requireNonNull(
                        notification,
                        "notification must not be null"))
                .toList();
        return validatedNotifications.stream()
                .map(notification -> notification.dispatch(successful))
                .toList();
    }
}
