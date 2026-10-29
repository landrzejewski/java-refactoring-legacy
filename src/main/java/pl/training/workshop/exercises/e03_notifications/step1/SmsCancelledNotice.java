package pl.training.workshop.exercises.e03_notifications.step1;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 1: SMS o odwolaniu seansu - tresc z EmailCancelledNotice, skracanie z SmsMovedNotice. */
public final class SmsCancelledNotice {
    private final Journal journal;
    private final Screening screening;
    private final String reason;

    public SmsCancelledNotice(Journal journal, Screening screening, String reason) {
        this.journal = journal;
        this.screening = screening;
        this.reason = reason;
    }

    public void send(Customer customer) {
        String subject = "Odwolany seans: " + screening.title();
        String body = "Seans " + screening.title() + " z " + screening.start() + " odwolany (" + reason
                + "). Zwrot w ciagu 7 dni.";
        String text = subject + ". " + body;
        if (text.length() > 70) {
            text = text.substring(0, 67) + "...";
        }
        journal.sms(customer.phone(), text);
    }
}
