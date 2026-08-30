package pl.training.module5.stage0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class NotificationCharacterizationTest {
    @Test
    void capturesEmailBehavior() {
        var notification = new EmailNotification(
                " msg-1 ",
                " ops ",
                " Deployment ready ",
                true);

        assertEquals("msg-1", notification.messageId());
        assertEquals(
                "msg-1|OPS|Deployment ready|EMAIL",
                notification.summary());
        assertEquals(
                "msg-1|OPS|Deployment ready|EMAIL|SENT",
                notification.dispatch(true));
        assertEquals(
                "msg-1|OPS|Deployment ready|EMAIL|FAILED",
                notification.dispatch(false));
    }

    @Test
    void capturesSmsReceiptBehavior() {
        var withReceipt = new SmsNotification(
                "msg-2",
                "ops",
                "Deploy now",
                true);
        var withoutReceipt = new SmsNotification(
                "msg-2",
                "ops",
                "Deploy now",
                false);

        assertEquals(
                "msg-2|OPS|Deploy now|SMS|SENT|RECEIPT",
                withReceipt.dispatch(true));
        assertEquals(
                "msg-2|OPS|Deploy now|SMS|FAILED",
                withReceipt.dispatch(false));
        assertEquals(
                "msg-2|OPS|Deploy now|SMS|SENT",
                withoutReceipt.dispatch(true));
    }

    @Test
    void capturesValidation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailNotification(" ", "ops", "body", false));
        assertThrows(
                NullPointerException.class,
                () -> new SmsNotification("msg", null, "body", false));
    }
}
