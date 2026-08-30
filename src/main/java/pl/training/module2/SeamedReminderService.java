package pl.training.module2;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class SeamedReminderService {
    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = currentDate();

        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }

        sendMessage(subscription.email(), subscription.renewalDate());
        return true;
    }

    protected LocalDate currentDate() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    protected void sendMessage(String email, LocalDate renewalDate) {
        System.out.printf(
                "Sent renewal reminder to %s for %s%n",
                email,
                renewalDate);
    }
}
