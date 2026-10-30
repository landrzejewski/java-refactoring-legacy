package pl.training.workshop.exercises.e03_notifications.start;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

public class CustomerNotifier implements ScreeningListener {

    private final Journal journal;

    public CustomerNotifier(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void onMoved(ScreeningMoved event) {
         event.customers()
                 .stream()
                 .map(customer -> createMovedNotification(customer, event.before(), event.after()))
                 .forEach(this::send);
    }

    @Override
    public void onCanceled(ScreeningCancelled event) {
        event.customers()
                .stream()
                .map(customer -> createMovedCancelledNotification(customer, event.screening(), event.reason()))
                .forEach(this::send);
    }

    private void send(Notification notification) {
        new PushNotifier(journal, new SmsNotifier(journal, new EmailNotifier(journal, new DefaultNotifier(journal)))).send(notification);
    }

    private Notification createMovedNotification(Customer customer, Screening before, Screening after) {
        var subject = "Zmiana seansu: " + after.title();
        var body = "Seans " + after.title() + " z " + before.start() + " przeniesiony na " + after.start() + ", sala " + after.hall() + ". Bilety zachowuja waznosc.";
        return  new Notification(customer, subject, body);
    }

    private Notification createMovedCancelledNotification(Customer customer, Screening screening, String reason) {
        var subject = "Odwolany seans: " + screening.title();
        var body = "Seans " + screening.title() + " z " + screening.start() + " odwolany (" + reason + "). Zwrot w ciagu 7 dni.";
        return  new Notification(customer, subject, body);
    }


}
