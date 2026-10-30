package pl.training.workshop.exercises.e03_notifications.step2;

import java.util.Objects;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 2: powiadamianie nie wybiera juz kanalu - dostaje poczatek lancucha dostarczania jako zwykly
 * kanal. Nie ma tu zadnego if po danych kontaktowych ani wpisu o niedostarczeniu.
 * Tresc kazdego rodzaju wiadomosci jest raz, a sposob wysylki (np. skracanie SMS) zostaje po stronie kanalu.
 */
public final class CustomerNotifier implements ScreeningListener {
    private final Channel delivery;

    public CustomerNotifier(Channel delivery) {
        this.delivery = Objects.requireNonNull(delivery, "delivery");
    }

    @Override
    public void screeningMoved(ScreeningMoved event) {
        String subject = "Zmiana seansu: " + event.after().title();
        String body = "Seans " + event.after().title() + " z " + event.before().start() + " przeniesiony na "
                + event.after().start() + ", sala " + event.after().hall() + ". Bilety zachowuja waznosc.";
        for (Customer customer : event.ticketHolders()) {
            delivery.deliver(customer, subject, body);
        }
    }

    @Override
    public void screeningCancelled(ScreeningCancelled event) {
        String subject = "Odwolany seans: " + event.screening().title();
        String body = "Seans " + event.screening().title() + " z " + event.screening().start() + " odwolany ("
                + event.reason() + "). Zwrot w ciagu 7 dni.";
        for (Customer customer : event.ticketHolders()) {
            delivery.deliver(customer, subject, body);
        }
    }
}
