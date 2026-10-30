package pl.training.workshop.exercises.e03_notifications.step2;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 2: kanal dostarczania (e-mail, SMS, push) i zarazem ogniwo lancucha dostarczania.
 * Zna tylko adresata, tytul i tresc; formatowanie pod ograniczenia kanalu jest po jego stronie.
 */
@FunctionalInterface
public interface Channel {
    void deliver(Customer customer, String subject, String body);
}
