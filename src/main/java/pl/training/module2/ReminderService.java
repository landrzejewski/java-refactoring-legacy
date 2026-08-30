package pl.training.module2;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

public final class ReminderService {
    private final Clock clock;
    private final ReminderGateway reminderGateway;

    public ReminderService(Clock clock, ReminderGateway reminderGateway) {
        this.clock = Objects.requireNonNull(clock);
        this.reminderGateway = Objects.requireNonNull(reminderGateway);
    }

    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = LocalDate.now(clock);

        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }

        reminderGateway.send(subscription.email(), subscription.renewalDate());
        return true;
    }

    public interface ReminderGateway {
        void send(String email, LocalDate renewalDate);
    }
}
