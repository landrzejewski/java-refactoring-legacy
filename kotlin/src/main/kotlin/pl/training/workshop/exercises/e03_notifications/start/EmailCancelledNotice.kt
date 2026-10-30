package pl.training.workshop.exercises.e03_notifications.start

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/** Start: e-mail o odwolaniu seansu. Nowy rodzaj wiadomosci (np. zmiana sali) to kolejne trzy klasy. */
class EmailCancelledNotice(private val journal: Journal, private val screening: Screening, private val reason: String) {
    fun send(customer: Customer) {
        val subject = "Odwolany seans: ${screening.title}"
        val body = "Seans ${screening.title} z ${screening.start} odwolany ($reason). Zwrot w ciagu 7 dni."
        journal.email(customer.email, subject, body)
    }
}
