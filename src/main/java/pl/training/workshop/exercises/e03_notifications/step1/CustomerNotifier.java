package pl.training.workshop.exercises.e03_notifications.step1;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 1: powiadamianie klientow wyjete ze schedulera do pierwszego odbiorcy zdarzen.
 * Lancuch if wyboru kanalu i szesc klas powiadomien przeniosly sie tu bez zmian - to temat kolejnych krokow.
 */
public final class CustomerNotifier implements ScreeningListener {
    private final Journal journal;

    public CustomerNotifier(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void screeningMoved(ScreeningMoved event) {
        for (Customer customer : event.ticketHolders()) {
            if (customer.pushToken() != null && !customer.pushToken().isBlank()
                    && !customer.pushToken().startsWith("expired-")) {
                new PushMovedNotice(journal, event.before(), event.after()).send(customer);
            } else if (customer.phone() != null && !customer.phone().isBlank()) {
                new SmsMovedNotice(journal, event.before(), event.after()).send(customer);
            } else if (customer.email() != null && !customer.email().isBlank()) {
                new EmailMovedNotice(journal, event.before(), event.after()).send(customer);
            } else {
                journal.note("UNDELIVERED " + customer.name() + " (brak kanalu)");
            }
        }
    }

    @Override
    public void screeningCancelled(ScreeningCancelled event) {
        for (Customer customer : event.ticketHolders()) {
            if (customer.pushToken() != null && !customer.pushToken().isBlank()
                    && !customer.pushToken().startsWith("expired-")) {
                new PushCancelledNotice(journal, event.screening(), event.reason()).send(customer);
            } else if (customer.phone() != null && !customer.phone().isBlank()) {
                new SmsCancelledNotice(journal, event.screening(), event.reason()).send(customer);
            } else if (customer.email() != null && !customer.email().isBlank()) {
                new EmailCancelledNotice(journal, event.screening(), event.reason()).send(customer);
            } else {
                journal.note("UNDELIVERED " + customer.name() + " (brak kanalu)");
            }
        }
    }
}
