package pl.training.module5.extractsubclass.after;

import java.time.Instant;
import java.util.Objects;

public final class ScheduledDeliveryJob extends DeliveryJob {
    private final Instant scheduledAt;

    ScheduledDeliveryJob(Instant scheduledAt) {
        this.scheduledAt = Objects.requireNonNull(
                scheduledAt,
                "scheduledAt must not be null");
    }

    @Override
    public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");

        if (now.isBefore(scheduledAt)) {
            return "WAITING_UNTIL " + scheduledAt;
        }
        return super.dispatchAt(now);
    }
}
