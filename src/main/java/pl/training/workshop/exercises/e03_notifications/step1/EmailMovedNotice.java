package pl.training.workshop.exercises.e03_notifications.step1;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 1: e-mail o przeniesieniu seansu. Tresc jest taka sama jak w SmsMovedNotice i PushMovedNotice. */
public final class EmailMovedNotice {
    private final Journal journal;
    private final Screening before;
    private final Screening after;

    public EmailMovedNotice(Journal journal, Screening before, Screening after) {
        this.journal = journal;
        this.before = before;
        this.after = after;
    }

    public void send(Customer customer) {
        String subject = "Zmiana seansu: " + after.title();
        String body = "Seans " + after.title() + " z " + before.start() + " przeniesiony na " + after.start()
                + ", sala " + after.hall() + ". Bilety zachowuja waznosc.";
        journal.email(customer.email(), subject, body);
    }
}
