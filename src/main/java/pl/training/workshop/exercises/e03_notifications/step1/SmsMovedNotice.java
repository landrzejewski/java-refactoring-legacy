package pl.training.workshop.exercises.e03_notifications.step1;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/**
 * Krok 1: SMS o przeniesieniu seansu. Tresc skopiowana z EmailMovedNotice, skracanie do 70 znakow
 * skopiowane z SmsCancelledNotice - zmiana limitu SMS wymaga poprawki w dwoch klasach.
 */
public final class SmsMovedNotice {
    private final Journal journal;
    private final Screening before;
    private final Screening after;

    public SmsMovedNotice(Journal journal, Screening before, Screening after) {
        this.journal = journal;
        this.before = before;
        this.after = after;
    }

    public void send(Customer customer) {
        String subject = "Zmiana seansu: " + after.title();
        String body = "Seans " + after.title() + " z " + before.start() + " przeniesiony na " + after.start()
                + ", sala " + after.hall() + ". Bilety zachowuja waznosc.";
        String text = subject + ". " + body;
        if (text.length() > 70) {
            text = text.substring(0, 67) + "...";
        }
        journal.sms(customer.phone(), text);
    }
}
