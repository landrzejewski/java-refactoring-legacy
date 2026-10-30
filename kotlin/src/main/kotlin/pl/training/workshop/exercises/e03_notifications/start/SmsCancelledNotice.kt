package pl.training.workshop.exercises.e03_notifications.start

import pl.training.workshop.exercises.e03_notifications.Customer
import pl.training.workshop.exercises.e03_notifications.Screening

/** Start: SMS o odwolaniu seansu - tresc z EmailCancelledNotice, skracanie z SmsMovedNotice. */
class SmsCancelledNotice(private val journal: Journal, private val screening: Screening, private val reason: String) {
    fun send(customer: Customer) {
        val subject = "Odwolany seans: ${screening.title}"
        val body = "Seans ${screening.title} z ${screening.start} odwolany ($reason). Zwrot w ciagu 7 dni."
        var text = "$subject. $body"
        if (text.length > 70) {
            text = text.substring(0, 67) + "..."
        }
        journal.sms(customer.phone, text)
    }
}
