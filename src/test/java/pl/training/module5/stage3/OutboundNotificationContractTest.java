package pl.training.module5.stage3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

final class OutboundNotificationContractTest {
    @Test
    void everyImplementationSatisfiesTheExtractedContract() {
        List<OutboundNotification> notifications = List.of(
                new EmailNotification("e-1", "ops", "Ready", false),
                new SmsNotification("s-1", "ops", "Ready", true));

        assertEquals(
                List.of(
                        "e-1|OPS|Ready|EMAIL|SENT",
                        "s-1|OPS|Ready|SMS|SENT|RECEIPT"),
                notifications.stream()
                        .map(notification -> notification.dispatch(true))
                        .toList());
    }

    @Test
    void batchDependsOnlyOnTheClientRole() {
        var batch = new NotificationBatch();
        List<OutboundNotification> notifications = List.of(
                new EmailNotification("e-1", "ops", "Ready"),
                new SmsNotification("s-1", "ops", "Ready", false));

        List<String> results = batch.dispatchAll(notifications, false);

        assertEquals(
                List.of(
                        "e-1|OPS|Ready|EMAIL|FAILED",
                        "s-1|OPS|Ready|SMS|FAILED"),
                results);
        assertThrows(
                UnsupportedOperationException.class,
                () -> results.add("unexpected"));
    }

    @Test
    void batchRejectsInvalidInputs() {
        var batch = new NotificationBatch();
        var dispatchCalls = new AtomicInteger();
        OutboundNotification recordingNotification = successful -> {
            dispatchCalls.incrementAndGet();
            return "dispatched";
        };

        assertThrows(
                NullPointerException.class,
                () -> batch.dispatchAll(null, true));
        assertThrows(
                NullPointerException.class,
                () -> batch.dispatchAll(
                        java.util.Arrays.asList(
                                recordingNotification,
                                null),
                        true));
        assertEquals(0, dispatchCalls.get());
    }
}
