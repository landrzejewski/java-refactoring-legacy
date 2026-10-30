package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer

/**
 * Krok 2: kanal dostarczania (e-mail, SMS, push) i zarazem ogniwo lancucha dostarczania.
 * Zna tylko adresata, tytul i tresc; formatowanie pod ograniczenia kanalu jest po jego stronie.
 */
fun interface Channel {
    fun deliver(customer: Customer, subject: String, body: String)
}
