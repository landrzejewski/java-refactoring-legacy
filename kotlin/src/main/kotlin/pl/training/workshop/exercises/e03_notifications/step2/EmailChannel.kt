package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer

/** Krok 2: kanal e-mail - tytul i tresc osobno, bez limitu dlugosci. */
class EmailChannel(private val journal: Journal) : Channel {
    override fun deliver(customer: Customer, subject: String, body: String) {
        journal.email(customer.email, subject, body)
    }
}
