package pl.training.workshop.exercises.e03_notifications.start;

public class SmsNotifier implements Notifier {

    private final Journal journal;
    private final Notifier nextNotifier;

    public SmsNotifier(Journal journal, Notifier nextNotifier) {
        this.journal = journal;
        this.nextNotifier = nextNotifier;
    }

    @Override
    public void send(Notification notification) {
        String text = notification.subject() + ". " + notification.body();
        if (text.length() > 70) {
            text = text.substring(0, 67) + "...";
        }
        var customer = notification.customer();
        if (customer.phone() != null && !customer.phone().isBlank()) {
            journal.sms(customer.phone(), text);
        } else  {
            nextNotifier.send(notification);
        }
    }

}
