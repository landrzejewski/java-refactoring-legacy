package pl.training.module5;

import java.time.Instant;
import java.util.List;

import pl.training.module5.stage3.EmailNotification;
import pl.training.module5.stage3.NotificationBatch;
import pl.training.module5.stage3.OutboundNotification;
import pl.training.module5.stage3.SmsNotification;

public final class Module5Examples {
    private Module5Examples() {
    }

    public static void main(String[] args) {
        String legacyNotification =
                new pl.training.module5.stage0.SmsNotification(
                        "msg-1", "ops", "Deployment ready", true)
                        .dispatch(true);
        String refactoredNotification = new SmsNotification(
                "msg-1", "ops", "Deployment ready", true)
                .dispatch(true);

        Instant scheduledAt = Instant.parse("2030-06-15T10:15:30Z");
        Instant now = scheduledAt.minusSeconds(1);
        String jobBefore =
                pl.training.module5.extractsubclass.before.DeliveryJob
                        .scheduled(scheduledAt)
                        .dispatchAt(now);
        String jobAfter =
                pl.training.module5.extractsubclass.after.DeliveryJob
                        .scheduled(scheduledAt)
                        .dispatchAt(now);

        String formattedBefore =
                new pl.training.module5.collapse.before.NotificationFormatter()
                        .format("ops@example.com", "Deployment ready");
        String formattedAfter =
                new pl.training.module5.collapse.after.NotificationFormatter()
                        .format("ops@example.com", "Deployment ready");

        var inheritedRecipients =
                new pl.training.module5.composition.before.RecipientList();
        var composedRecipients =
                new pl.training.module5.composition.after.RecipientList();
        inheritedRecipients.add("ops@example.com");
        composedRecipients.add("ops@example.com");

        List<OutboundNotification> batch = List.of(
                new EmailNotification("mail-1", "ops", "Ready"),
                new SmsNotification("sms-1", "ops", "Ready", true));

        System.out.println("Hierarchy stages equivalent: "
                + legacyNotification.equals(refactoredNotification));
        System.out.println("Extract subclass equivalent: "
                + jobBefore.equals(jobAfter));
        System.out.println("Collapse hierarchy equivalent: "
                + formattedBefore.equals(formattedAfter));
        System.out.println("Composition client behavior equivalent: "
                + inheritedRecipients.snapshot()
                        .equals(composedRecipients.snapshot()));
        System.out.println("Batch results: "
                + new NotificationBatch().dispatchAll(batch, true));
    }
}
