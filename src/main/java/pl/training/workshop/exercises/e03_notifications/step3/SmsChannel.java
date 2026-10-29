package pl.training.workshop.exercises.e03_notifications.step3;

import pl.training.workshop.exercises.e03_notifications.Customer;

/** Krok 3: kanal SMS - sklejenie tytulu z trescia i limit 70 znakow sa teraz w jednym miejscu. */
public final class SmsChannel implements Channel {
    private static final int MAX_LENGTH = 70;

    private final Journal journal;

    public SmsChannel(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void deliver(Customer customer, String subject, String body) {
        String text = subject + ". " + body;
        if (text.length() > MAX_LENGTH) {
            text = text.substring(0, MAX_LENGTH - 3) + "...";
        }
        journal.sms(customer.phone(), text);
    }
}
