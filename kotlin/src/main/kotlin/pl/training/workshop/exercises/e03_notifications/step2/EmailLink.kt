package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer

/** Krok 2: ogniwo e-mail - obsluguje klienta z adresem. */
class EmailLink(private val email: EmailChannel, private val next: Channel) : Channel {
    override fun deliver(customer: Customer, subject: String, body: String) {
        if (!customer.email.isNullOrBlank()) {
            email.deliver(customer, subject, body)
        } else {
            next.deliver(customer, subject, body)
        }
    }
}
