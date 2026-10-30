package pl.training.workshop.exercises.e03_notifications.start

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/** Start: powiadomienie push o odwolaniu seansu - szosta klasa z tej samej siatki kanal x rodzaj. */
class PushCancelledNotice(private val journal: Journal, private val screening: Screening, private val reason: String) {
    fun send(customer: Customer) {
        val subject = "Odwolany seans: ${screening.title}"
        val body = "Seans ${screening.title} z ${screening.start} odwolany ($reason). Zwrot w ciagu 7 dni."
        journal.push(customer.pushToken, subject, body)
    }
}
