package pl.training.module5.extractsubclass.after;

import java.time.Instant;
import java.util.Objects;

public class DeliveryJob {
    DeliveryJob() {
    }

    public static DeliveryJob immediate() {
        return new DeliveryJob();
    }

    public static DeliveryJob scheduled(Instant scheduledAt) {
        return new ScheduledDeliveryJob(scheduledAt);
    }

    public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        return "SENT";
    }
}
