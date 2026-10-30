package pl.training.workshop.exercises.e03_notifications.step2

import pl.training.workshop.exercises.e03_notifications.Customer

/** Krok 2: ogniwo SMS - obsluguje klienta z numerem telefonu. */
class SmsLink(private val sms: SmsChannel, private val next: Channel) : Channel {
    override fun deliver(customer: Customer, subject: String, body: String) {
        if (!customer.phone.isNullOrBlank()) {
            sms.deliver(customer, subject, body)
        } else {
            next.deliver(customer, subject, body)
        }
    }
}
