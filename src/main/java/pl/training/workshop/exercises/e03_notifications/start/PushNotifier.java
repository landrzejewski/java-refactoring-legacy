package pl.training.workshop.exercises.e03_notifications.start;

public class PushNotifier implements Notifier {

    private final Journal journal;
    private final Notifier nextNotifier;

    public PushNotifier(Journal journal, Notifier nextNotifier) {
        this.journal = journal;
        this.nextNotifier = nextNotifier;
    }

    @Override
    public void send(Notification notification) {
        var customer = notification.customer();
        if (customer.pushToken() != null && !customer.pushToken().isBlank() && !customer.pushToken().startsWith("expired-")) {
            journal.push(notification.customer().pushToken(), notification.subject(), notification.body());
        } else {
            nextNotifier.send(notification);
        }
    }

}
