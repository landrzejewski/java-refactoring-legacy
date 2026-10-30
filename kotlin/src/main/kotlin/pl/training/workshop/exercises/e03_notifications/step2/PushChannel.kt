package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer

/** Krok 2: kanal push. */
class PushChannel(private val journal: Journal) : Channel {
    override fun deliver(customer: Customer, subject: String, body: String) {
        journal.push(customer.pushToken, subject, body)
    }
}
