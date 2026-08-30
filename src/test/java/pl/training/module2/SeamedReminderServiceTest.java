package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

final class SeamedReminderServiceTest {
    @Test
    void sendsReminderForRenewalExactlySevenDaysAway() {
        TestableReminderService service = new TestableReminderService(
                LocalDate.of(2026, 8, 30));
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 6));

        boolean sent = service.sendRenewalReminder(subscription);

        assertTrue(sent);
        assertEquals(
                List.of("developer@example.com|2026-09-06"),
                service.sentMessages());
    }

    @Test
    void doesNotSendReminderMoreThanSevenDaysBeforeRenewal() {
        TestableReminderService service = new TestableReminderService(
                LocalDate.of(2026, 8, 30));
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 7));

        boolean sent = service.sendRenewalReminder(subscription);

        assertFalse(sent);
        assertEquals(List.of(), service.sentMessages());
    }

    @Test
    void documentsCurrentBehaviorForPastRenewalDate() {
        TestableReminderService service = new TestableReminderService(
                LocalDate.of(2026, 8, 30));
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 8, 29));

        boolean sent = service.sendRenewalReminder(subscription);

        assertTrue(sent);
        assertEquals(
                List.of("developer@example.com|2026-08-29"),
                service.sentMessages());
    }

    private static final class TestableReminderService extends SeamedReminderService {
        private final LocalDate today;
        private final List<String> messages = new ArrayList<>();

        private TestableReminderService(LocalDate today) {
            this.today = today;
        }

        @Override
        protected LocalDate currentDate() {
            return today;
        }

        @Override
        protected void sendMessage(String email, LocalDate renewalDate) {
            messages.add(email + "|" + renewalDate);
        }

        List<String> sentMessages() {
            return List.copyOf(messages);
        }
    }
}
