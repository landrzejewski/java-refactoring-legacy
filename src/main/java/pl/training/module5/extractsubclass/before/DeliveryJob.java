package pl.training.module5.extractsubclass.before;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class DeliveryJob {
    private static final String SENT = "SENT";
    private static final String WAITING_UNTIL = "WAITING_UNTIL ";

    private final Optional<Instant> scheduledAt;

    private DeliveryJob(Optional<Instant> scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public static DeliveryJob immediate() {
        return new DeliveryJob(Optional.empty());
    }

    public static DeliveryJob scheduled(Instant scheduledAt) {
        return new DeliveryJob(Optional.of(Objects.requireNonNull(
                scheduledAt,
                "scheduledAt must not be null")));
    }

    public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");

        return scheduledAt
                .filter(now::isBefore)
                .map(instant -> WAITING_UNTIL + instant)
                .orElse(SENT);
    }
}
