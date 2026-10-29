package pl.training.workshop.exercises.e03_notifications.step3;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 3: kanal dostarczania - druga os zmiennosci (e-mail, SMS, push), oddzielona od rodzaju wiadomosci.
 * Zna tylko adresata, tytul i tresc; formatowanie pod ograniczenia kanalu jest po jego stronie.
 */
@FunctionalInterface
public interface Channel {
    void deliver(Customer customer, String subject, String body);
}
