package pl.training.workshop.exercises.e03_notifications.step1;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 1: powiadomienie push o przeniesieniu seansu - trzecia kopia tej samej tresci. */
public final class PushMovedNotice {
    private final Journal journal;
    private final Screening before;
    private final Screening after;

    public PushMovedNotice(Journal journal, Screening before, Screening after) {
        this.journal = journal;
        this.before = before;
        this.after = after;
    }

    public void send(Customer customer) {
        String subject = "Zmiana seansu: " + after.title();
        String body = "Seans " + after.title() + " z " + before.start() + " przeniesiony na " + after.start()
                + ", sala " + after.hall() + ". Bilety zachowuja waznosc.";
        journal.push(customer.pushToken(), subject, body);
    }
}
