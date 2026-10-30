package pl.training.workshop.exercises.e03_notifications.start;

public class DefaultNotifier implements Notifier {

    private final Journal journal;
    private Notifier nextNotifier;

    public DefaultNotifier(Journal journal) {
        this.journal = journal;
    }

    public void setNextNotifier(Notifier nextNotifier) {
        this.nextNotifier = nextNotifier;
    }

    @Override
    public void send(Notification notification) {
        journal.note("UNDELIVERED " + notification.customer().name() + " (brak kanalu)");
    }

}
