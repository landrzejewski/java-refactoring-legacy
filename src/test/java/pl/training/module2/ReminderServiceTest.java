package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

final class ReminderServiceTest {
    @Test
    void usesInjectedClockAndGateway() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-30T10:00:00Z"),
                ZoneOffset.UTC);
        RecordingReminderGateway gateway = new RecordingReminderGateway();
        ReminderService service = new ReminderService(clock, gateway);
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 6));

        boolean sent = service.sendRenewalReminder(subscription);

        assertTrue(sent);
        assertEquals(
                List.of("developer@example.com|2026-09-06"),
                gateway.messages());
    }

    @Test
    void doesNotSendReminderMoreThanSevenDaysBeforeRenewal() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-30T10:00:00Z"),
                ZoneOffset.UTC);
        RecordingReminderGateway gateway = new RecordingReminderGateway();
        ReminderService service = new ReminderService(clock, gateway);
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 7));

        boolean sent = service.sendRenewalReminder(subscription);

        assertFalse(sent);
        assertEquals(List.of(), gateway.messages());
    }

    @Test
    void preservesCurrentBehaviorForPastRenewalDate() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-30T10:00:00Z"),
                ZoneOffset.UTC);
        RecordingReminderGateway gateway = new RecordingReminderGateway();
        ReminderService service = new ReminderService(clock, gateway);
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 8, 29));

        boolean sent = service.sendRenewalReminder(subscription);

        assertTrue(sent);
        assertEquals(
                List.of("developer@example.com|2026-08-29"),
                gateway.messages());
    }

    private static final class RecordingReminderGateway
            implements ReminderService.ReminderGateway {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void send(String email, LocalDate renewalDate) {
            messages.add(email + "|" + renewalDate);
        }

        List<String> messages() {
            return List.copyOf(messages);
        }
    }
}
