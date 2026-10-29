package pl.training.workshop.exercises.e03_notifications.step3;

import java.util.Objects;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 3: powiadamianie nie wybiera juz kanalu - dostaje poczatek lancucha dostarczania jako zwykly
 * kanal. Nie ma tu zadnego if po danych kontaktowych ani wpisu o niedostarczeniu.
 */
public final class CustomerNotifier implements ScreeningListener {
    private final Channel delivery;

    public CustomerNotifier(Channel delivery) {
        this.delivery = Objects.requireNonNull(delivery, "delivery");
    }

    @Override
    public void screeningMoved(ScreeningMoved event) {
        Notice notice = new MovedNotice(delivery, event.before(), event.after());
        for (Customer customer : event.ticketHolders()) {
            notice.send(customer);
        }
    }

    @Override
    public void screeningCancelled(ScreeningCancelled event) {
        Notice notice = new CancelledNotice(delivery, event.screening(), event.reason());
        for (Customer customer : event.ticketHolders()) {
            notice.send(customer);
        }
    }
}
