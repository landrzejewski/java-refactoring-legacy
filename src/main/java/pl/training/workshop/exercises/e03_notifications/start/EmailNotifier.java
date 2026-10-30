package pl.training.workshop.exercises.e03_notifications.start;

public class EmailNotifier implements Notifier {

    private final Journal journal;
    private final Notifier nextNotifier;

    public EmailNotifier(Journal journal, Notifier nextNotifier) {
        this.journal = journal;
        this.nextNotifier = nextNotifier;
    }

    @Override
    public void send(Notification notification) {
        var customer = notification.customer();
        if (customer.email() != null && !customer.email().isBlank()) {
            journal.email(customer.email(), notification.subject(), notification.body());
        } else {
            nextNotifier.send(notification);
        }
    }

}
