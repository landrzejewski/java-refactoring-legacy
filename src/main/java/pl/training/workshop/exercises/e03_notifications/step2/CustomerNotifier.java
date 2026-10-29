package pl.training.workshop.exercises.e03_notifications.step2;

import java.util.Optional;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 2: powiadamianie sklada rodzaj wiadomosci z kanalem. Lancuch if wyboru kanalu zostal jeden
 * (metoda channelFor), ale jego kolejnosc i warunki dalej sa zaszyte tutaj - to temat kroku 3.
 */
public final class CustomerNotifier implements ScreeningListener {
    private final Journal journal;
    private final Channel push;
    private final Channel sms;
    private final Channel email;

    public CustomerNotifier(Journal journal) {
        this.journal = journal;
        this.push = new PushChannel(journal);
        this.sms = new SmsChannel(journal);
        this.email = new EmailChannel(journal);
    }

    @Override
    public void screeningMoved(ScreeningMoved event) {
        for (Customer customer : event.ticketHolders()) {
            channelFor(customer).ifPresentOrElse(
                    channel -> new MovedNotice(channel, event.before(), event.after()).send(customer),
                    () -> undelivered(customer));
        }
    }

    @Override
    public void screeningCancelled(ScreeningCancelled event) {
        for (Customer customer : event.ticketHolders()) {
            channelFor(customer).ifPresentOrElse(
                    channel -> new CancelledNotice(channel, event.screening(), event.reason()).send(customer),
                    () -> undelivered(customer));
        }
    }

    private Optional<Channel> channelFor(Customer customer) {
        if (customer.pushToken() != null && !customer.pushToken().isBlank()
                && !customer.pushToken().startsWith("expired-")) {
            return Optional.of(push);
        }
        if (customer.phone() != null && !customer.phone().isBlank()) {
            return Optional.of(sms);
        }
        if (customer.email() != null && !customer.email().isBlank()) {
            return Optional.of(email);
        }
        return Optional.empty();
    }

    private void undelivered(Customer customer) {
        journal.note("UNDELIVERED " + customer.name() + " (brak kanalu)");
    }
}
