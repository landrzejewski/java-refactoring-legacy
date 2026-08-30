package pl.training.module2;

import java.time.LocalDate;
import java.time.ZoneOffset;

public final class LegacyReminderService {
    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }

        System.out.printf(
                "Sent renewal reminder to %s for %s%n",
                subscription.email(),
                subscription.renewalDate());
        return true;
    }
}
